package dev.raffadevs.licitacoes.documento;

import dev.raffadevs.licitacoes.common.RecursoNaoEncontradoException;
import dev.raffadevs.licitacoes.documento.dto.DocumentoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository) {
        this.documentoRepository = documentoRepository;
    }

    @Transactional(readOnly = true)
    public DocumentoResponse buscar(Long id) {
        return DocumentoResponse.from(buscarOuFalhar(id));
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> listar() {
        return documentoRepository.findAll().stream().map(DocumentoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public Documento buscarOuFalhar(Long id) {
        return documentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Documento não encontrado"));
    }
}
