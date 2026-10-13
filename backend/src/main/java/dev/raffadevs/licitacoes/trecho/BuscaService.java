package dev.raffadevs.licitacoes.trecho;

import dev.raffadevs.licitacoes.config.BuscaProperties;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Busca semântica restrita a UM documento por vez: o filtro por documentoId
 * evita que uma pergunta sobre o edital A seja respondida com trechos do
 * edital B só porque ficaram mais próximos no espaço vetorial.
 */
@Service
@EnableConfigurationProperties(BuscaProperties.class)
public class BuscaService {

    private final VectorStore vectorStore;
    private final BuscaProperties properties;

    public BuscaService(VectorStore vectorStore, BuscaProperties properties) {
        this.vectorStore = vectorStore;
        this.properties = properties;
    }

    public List<TrechoEncontrado> buscar(Long documentoId, String pergunta) {
        Filter.Expression filtro = new Filter.Expression(
                Filter.ExpressionType.EQ,
                new Filter.Key(TrechoMetadata.DOCUMENTO_ID),
                new Filter.Value(documentoId));

        SearchRequest request = SearchRequest.builder()
                .query(pergunta)
                .topK(properties.getLimiteTrechos())
                .similarityThreshold(properties.getSimilaridadeMinima())
                .filterExpression(filtro)
                .build();

        List<Document> encontrados = vectorStore.similaritySearch(request);
        if (encontrados == null) {
            return List.of();
        }

        return encontrados.stream()
                .map(doc -> new TrechoEncontrado(
                        ((Number) doc.getMetadata().get(TrechoMetadata.PAGINA)).intValue(),
                        doc.getText(),
                        doc.getScore() != null ? doc.getScore() : 0.0))
                .toList();
    }
}
