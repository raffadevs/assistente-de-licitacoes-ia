package dev.raffadevs.licitacoes.conversa;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.licitacoes.common.RecursoNaoEncontradoException;
import dev.raffadevs.licitacoes.conversa.dto.MensagemResponse;
import dev.raffadevs.licitacoes.conversa.dto.RespostaChat;
import dev.raffadevs.licitacoes.documento.DocumentoService;
import dev.raffadevs.licitacoes.trecho.BuscaService;
import dev.raffadevs.licitacoes.trecho.TrechoEncontrado;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Implementa o padrão RAG com uma garantia deliberada: as páginas citadas na
 * resposta não são extraídas por regex do texto livre gerado pelo modelo
 * (frágil — depende do modelo formatar exatamente como pedido), mas sim
 * registradas diretamente a partir dos trechos realmente recuperados pela
 * busca semântica. Se o modelo usou o contexto fornecido (instrução
 * explícita no prompt), a citação é estruturalmente garantida.
 *
 * Quando a busca não encontra nenhum trecho acima do limite de similaridade,
 * a pergunta nunca chega ao modelo — a recusa ("não encontrei essa
 * informação") é devolvida diretamente. Isso é mais forte do que confiar no
 * modelo para se recusar sozinho, e evita uma chamada de IA desnecessária.
 */
@Service
public class ConversaService {

    private static final String MENSAGEM_SEM_RESPOSTA =
            "Não encontrei essa informação no edital. Tente reformular a pergunta ou verifique se o documento já terminou de ser processado.";

    private static final String PROMPT_SISTEMA = """
            Você é um assistente que responde perguntas sobre editais de licitação.
            Responda SOMENTE com base nos trechos do edital fornecidos abaixo, cada um
            marcado com o número da página de origem. Não invente informação que não
            esteja nos trechos. Ao usar uma informação, mencione a página entre
            colchetes, por exemplo [p. 12]. Se os trechos não forem suficientes para
            responder, diga claramente que a informação não foi encontrada no edital,
            em vez de tentar adivinhar.

            Trechos do edital:
            %s
            """;

    private final ConversaRepository conversaRepository;
    private final MensagemRepository mensagemRepository;
    private final DocumentoService documentoService;
    private final BuscaService buscaService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public ConversaService(ConversaRepository conversaRepository, MensagemRepository mensagemRepository,
                            DocumentoService documentoService, BuscaService buscaService,
                            ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.conversaRepository = conversaRepository;
        this.mensagemRepository = mensagemRepository;
        this.documentoService = documentoService;
        this.buscaService = buscaService;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Long criar(Long documentoId) {
        documentoService.buscarOuFalhar(documentoId); // 404 cedo se o documento não existir
        return conversaRepository.save(new Conversa(documentoId)).getId();
    }

    @Transactional
    public RespostaChat perguntar(Long conversaId, String pergunta) {
        Conversa conversa = buscarConversaOuFalhar(conversaId);
        mensagemRepository.save(new Mensagem(conversaId, Papel.USUARIO, pergunta, null));

        List<TrechoEncontrado> trechos = buscaService.buscar(conversa.getDocumentoId(), pergunta);

        if (trechos.isEmpty()) {
            mensagemRepository.save(new Mensagem(conversaId, Papel.ASSISTENTE, MENSAGEM_SEM_RESPOSTA, "[]"));
            return new RespostaChat(conversaId, MENSAGEM_SEM_RESPOSTA, List.of(), true);
        }

        String contexto = montarContexto(trechos);
        String resposta = chatClient.prompt()
                .system(PROMPT_SISTEMA.formatted(contexto))
                .user(pergunta)
                .call()
                .content();

        List<Integer> paginasCitadas = trechos.stream()
                .map(TrechoEncontrado::pagina)
                .distinct()
                .sorted()
                .toList();

        mensagemRepository.save(new Mensagem(conversaId, Papel.ASSISTENTE, resposta,
                escreverJson(paginasCitadas)));

        return new RespostaChat(conversaId, resposta, paginasCitadas, false);
    }

    @Transactional(readOnly = true)
    public List<MensagemResponse> listarMensagens(Long conversaId) {
        buscarConversaOuFalhar(conversaId);
        return mensagemRepository.findByConversaIdOrderByCriadaEmAsc(conversaId).stream()
                .map(m -> MensagemResponse.from(m, objectMapper))
                .toList();
    }

    private Conversa buscarConversaOuFalhar(Long conversaId) {
        return conversaRepository.findById(conversaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conversa não encontrada"));
    }

    private String montarContexto(List<TrechoEncontrado> trechos) {
        StringBuilder sb = new StringBuilder();
        for (TrechoEncontrado trecho : trechos.stream()
                .sorted(Comparator.comparingInt(TrechoEncontrado::pagina)).toList()) {
            sb.append("[p. ").append(trecho.pagina()).append("] ").append(trecho.texto()).append("\n\n");
        }
        return sb.toString();
    }

    private String escreverJson(List<Integer> paginas) {
        try {
            return objectMapper.writeValueAsString(paginas);
        } catch (Exception e) {
            return "[]";
        }
    }
}
