package dev.raffadevs.licitacoes.avaliacao;

import dev.raffadevs.licitacoes.avaliacao.dto.AvaliacaoResponse;
import dev.raffadevs.licitacoes.avaliacao.dto.CasoAvaliacaoRequest;
import dev.raffadevs.licitacoes.documento.DocumentoService;
import dev.raffadevs.licitacoes.trecho.BuscaService;
import dev.raffadevs.licitacoes.trecho.TrechoEncontrado;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Roda um conjunto de perguntas com resposta esperada conhecida contra o
 * mesmo pipeline de busca + geração usado pelo chat (sem persistir em
 * conversa/mensagem — uma execução de avaliação não é uma conversa de
 * verdade), e registra se cada resposta obtida "passou" pelo critério
 * determinístico de {@link AvaliacaoChecker}.
 */
@Service
public class AvaliacaoService {

    private static final String MENSAGEM_SEM_RESPOSTA = "Não encontrei essa informação no edital.";

    private static final String PROMPT_SISTEMA = """
            Você é um assistente que responde perguntas sobre editais de licitação.
            Responda SOMENTE com base nos trechos do edital fornecidos abaixo, cada um
            marcado com o número da página de origem. Não invente informação que não
            esteja nos trechos. Se os trechos não forem suficientes, diga que a
            informação não foi encontrada no edital.

            Trechos do edital:
            %s
            """;

    private final AvaliacaoRepository avaliacaoRepository;
    private final DocumentoService documentoService;
    private final BuscaService buscaService;
    private final ChatClient chatClient;

    @Value("${spring.ai.ollama.chat.model}")
    private String modeloConfigurado;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, DocumentoService documentoService,
                             BuscaService buscaService, ChatClient.Builder chatClientBuilder) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.documentoService = documentoService;
        this.buscaService = buscaService;
        this.chatClient = chatClientBuilder.build();
    }

    @Transactional
    public List<AvaliacaoResponse> executar(Long documentoId, CasoAvaliacaoRequest request) {
        documentoService.buscarOuFalhar(documentoId);

        return request.casos().stream()
                .map(caso -> executarCaso(documentoId, caso))
                .map(avaliacaoRepository::save)
                .map(AvaliacaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listar(Long documentoId) {
        return avaliacaoRepository.findByDocumentoIdOrderByExecutadoEmDesc(documentoId).stream()
                .map(AvaliacaoResponse::from)
                .toList();
    }

    private Avaliacao executarCaso(Long documentoId, CasoAvaliacaoRequest.Caso caso) {
        List<TrechoEncontrado> trechos = buscaService.buscar(documentoId, caso.pergunta());

        String respostaObtida = trechos.isEmpty()
                ? MENSAGEM_SEM_RESPOSTA
                : responder(caso.pergunta(), trechos);

        boolean passou = AvaliacaoChecker.passou(caso.respostaEsperada(), respostaObtida);
        return new Avaliacao(documentoId, caso.pergunta(), caso.respostaEsperada(), respostaObtida, passou,
                modeloConfigurado);
    }

    private String responder(String pergunta, List<TrechoEncontrado> trechos) {
        String contexto = trechos.stream()
                .sorted(Comparator.comparingInt(TrechoEncontrado::pagina))
                .map(t -> "[p. " + t.pagina() + "] " + t.texto())
                .reduce("", (a, b) -> a + b + "\n\n");

        return chatClient.prompt()
                .system(PROMPT_SISTEMA.formatted(contexto))
                .user(pergunta)
                .call()
                .content();
    }
}
