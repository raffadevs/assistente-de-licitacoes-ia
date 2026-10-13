package dev.raffadevs.licitacoes.ingestao;

import dev.raffadevs.licitacoes.documento.Documento;
import dev.raffadevs.licitacoes.documento.DocumentoRepository;
import dev.raffadevs.licitacoes.trecho.TrechoMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Worker assíncrono do pipeline de ingestão, separado de {@link IngestaoService}
 * porque {@code @Async} depende de um proxy do Spring — chamar um método
 * assíncrono de dentro da própria classe (auto-invocação) ignora o proxy e
 * executa de forma síncrona, silenciosamente. Isolar numa classe à parte, e
 * injetá-la, garante que a chamada passe pelo proxy de verdade.
 */
@Component
public class IngestaoProcessor {

    private static final Logger log = LoggerFactory.getLogger(IngestaoProcessor.class);

    private final DocumentoRepository documentoRepository;
    private final PdfExtractorService pdfExtractorService;
    private final ChunkingService chunkingService;
    private final VectorStore vectorStore;

    public IngestaoProcessor(DocumentoRepository documentoRepository, PdfExtractorService pdfExtractorService,
                              ChunkingService chunkingService, VectorStore vectorStore) {
        this.documentoRepository = documentoRepository;
        this.pdfExtractorService = pdfExtractorService;
        this.chunkingService = chunkingService;
        this.vectorStore = vectorStore;
    }

    @Async("ingestaoExecutor")
    public void processarEmSegundoPlano(Long documentoId, byte[] conteudo) {
        try {
            processar(documentoId, conteudo);
        } catch (Exception e) {
            log.warn("Falha ao processar documento {}: {}", documentoId, e.getMessage());
            marcarFalha(documentoId, resumirErro(e));
        }
    }

    @Transactional
    void processar(Long documentoId, byte[] conteudo) throws Exception {
        Documento documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalStateException("Documento removido antes do processamento"));
        documento.marcarProcessando();

        List<PaginaTexto> paginas = pdfExtractorService.extrairPorPagina(conteudo);
        List<TrechoBruto> trechos = chunkingService.dividir(paginas);

        if (!trechos.isEmpty()) {
            List<Document> documentosParaIndexar = trechos.stream()
                    .map(t -> new Document(t.texto(), Map.of(
                            TrechoMetadata.DOCUMENTO_ID, documentoId,
                            TrechoMetadata.PAGINA, t.pagina())))
                    .toList();
            vectorStore.add(documentosParaIndexar);
        }

        documento.marcarConcluido(paginas.size());
    }

    @Transactional
    void marcarFalha(Long documentoId, String motivo) {
        documentoRepository.findById(documentoId).ifPresent(d -> d.marcarFalhou(motivo));
    }

    private String resumirErro(Exception e) {
        String mensagem = e.getMessage();
        return mensagem == null ? e.getClass().getSimpleName() : mensagem;
    }
}
