package dev.raffadevs.licitacoes.resumo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.licitacoes.common.RecursoNaoEncontradoException;
import dev.raffadevs.licitacoes.documento.DocumentoService;
import dev.raffadevs.licitacoes.resumo.dto.ResumoResponse;
import dev.raffadevs.licitacoes.trecho.BuscaService;
import dev.raffadevs.licitacoes.trecho.TrechoEncontrado;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Extração estruturada: em vez de perguntar item por item, busca os trechos
 * mais relevantes para uma consulta ampla (objeto, valor, modalidade, prazos,
 * habilitação) e pede ao modelo para devolver tudo de uma vez num formato
 * tipado — mais barato (uma chamada, não cinco) e mais consistente (um único
 * contexto coerente, não cinco respostas que podem se contradizer).
 */
@Service
public class ResumoService {

    private static final String CONSULTA_AMPLA =
            "objeto da licitação, valor estimado, modalidade, prazos e documentos de habilitação exigidos";

    private static final String PROMPT_SISTEMA = """
            Você extrai dados estruturados de editais de licitação a partir dos
            trechos abaixo. Preencha apenas com o que estiver explícito nos
            trechos; deixe um campo vazio (string vazia, lista vazia ou valor
            nulo) em vez de inventar informação ausente.

            Trechos do edital:
            %s
            """;

    private final ResumoEditalRepository resumoEditalRepository;
    private final DocumentoService documentoService;
    private final BuscaService buscaService;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public ResumoService(ResumoEditalRepository resumoEditalRepository, DocumentoService documentoService,
                          BuscaService buscaService, ChatClient.Builder chatClientBuilder,
                          ObjectMapper objectMapper) {
        this.resumoEditalRepository = resumoEditalRepository;
        this.documentoService = documentoService;
        this.buscaService = buscaService;
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ResumoResponse gerar(Long documentoId) {
        documentoService.buscarOuFalhar(documentoId);

        List<TrechoEncontrado> trechos = buscaService.buscar(documentoId, CONSULTA_AMPLA);
        String contexto = trechos.stream()
                .sorted(Comparator.comparingInt(TrechoEncontrado::pagina))
                .map(t -> "[p. " + t.pagina() + "] " + t.texto())
                .reduce("", (a, b) -> a + b + "\n\n");

        ResumoExtraido extraido = chatClient.prompt()
                .system(PROMPT_SISTEMA.formatted(contexto))
                .user("Extraia o resumo estruturado deste edital.")
                .call()
                .entity(ResumoExtraido.class);

        ResumoEdital resumo = resumoEditalRepository.findById(documentoId)
                .map(existente -> {
                    existente.atualizar(extraido.objeto(), extraido.valorEstimado(), extraido.modalidade(),
                            escreverJson(extraido.prazos()), escreverJson(extraido.documentosExigidos()));
                    return existente;
                })
                .orElseGet(() -> new ResumoEdital(documentoId, extraido.objeto(), extraido.valorEstimado(),
                        extraido.modalidade(), escreverJson(extraido.prazos()),
                        escreverJson(extraido.documentosExigidos())));

        return ResumoResponse.from(resumoEditalRepository.save(resumo), objectMapper);
    }

    @Transactional(readOnly = true)
    public ResumoResponse buscar(Long documentoId) {
        ResumoEdital resumo = resumoEditalRepository.findById(documentoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Nenhum resumo gerado ainda para este documento — use POST para gerar"));
        return ResumoResponse.from(resumo, objectMapper);
    }

    private String escreverJson(List<String> valores) {
        try {
            return objectMapper.writeValueAsString(valores == null ? List.of() : valores);
        } catch (Exception e) {
            return "[]";
        }
    }
}
