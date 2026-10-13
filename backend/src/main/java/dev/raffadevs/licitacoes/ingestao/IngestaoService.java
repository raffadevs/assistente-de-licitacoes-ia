package dev.raffadevs.licitacoes.ingestao;

import dev.raffadevs.licitacoes.documento.Documento;
import dev.raffadevs.licitacoes.documento.DocumentoRepository;
import dev.raffadevs.licitacoes.documento.dto.DocumentoResponse;
import org.springframework.stereotype.Service;

/**
 * Ponto de entrada síncrono: salva o registro do documento e devolve a
 * resposta imediatamente, delegando o trabalho pesado (extração, chunking,
 * embeddings) a {@link IngestaoProcessor} em segundo plano.
 *
 * Deliberadamente SEM @Transactional aqui: se este método abrisse uma
 * transação própria, o commit só aconteceria ao retornar — mas a chamada
 * assíncrona abaixo já dispara antes disso, e o worker em outra thread
 * poderia não encontrar a linha ainda não commitada. Sem a anotação, o
 * save() do Spring Data já commita sozinho (transação implícita do próprio
 * repositório), garantindo que o worker sempre encontre o documento.
 */
@Service
public class IngestaoService {

    private final DocumentoRepository documentoRepository;
    private final IngestaoProcessor ingestaoProcessor;

    public IngestaoService(DocumentoRepository documentoRepository, IngestaoProcessor ingestaoProcessor) {
        this.documentoRepository = documentoRepository;
        this.ingestaoProcessor = ingestaoProcessor;
    }

    public DocumentoResponse iniciar(String titulo, String orgao, String nomeArquivo, byte[] conteudo) {
        Documento documento = documentoRepository.save(new Documento(titulo, orgao, nomeArquivo));
        ingestaoProcessor.processarEmSegundoPlano(documento.getId(), conteudo);
        return DocumentoResponse.from(documento);
    }
}
