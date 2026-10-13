package dev.raffadevs.licitacoes.documento;

import dev.raffadevs.licitacoes.documento.dto.DocumentoResponse;
import dev.raffadevs.licitacoes.ingestao.IngestaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
@Tag(name = "Documentos", description = "Upload de editais em PDF e consulta do status de processamento")
public class DocumentoController {

    private final DocumentoService documentoService;
    private final IngestaoService ingestaoService;

    public DocumentoController(DocumentoService documentoService, IngestaoService ingestaoService) {
        this.documentoService = documentoService;
        this.ingestaoService = ingestaoService;
    }

    @PostMapping(consumes = "multipart/form-data")
    @Operation(summary = "Envia um PDF de edital para processamento (extração, chunking e indexação em segundo plano)")
    public ResponseEntity<DocumentoResponse> enviar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @RequestParam @NotBlank String titulo,
            @RequestParam(required = false) String orgao) throws IOException {
        DocumentoResponse resposta = ingestaoService.iniciar(titulo, orgao, arquivo.getOriginalFilename(),
                arquivo.getBytes());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Lista os documentos enviados")
    public ResponseEntity<List<DocumentoResponse>> listar() {
        return ResponseEntity.ok(documentoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulta um documento e seu status de processamento")
    public ResponseEntity<DocumentoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(documentoService.buscar(id));
    }
}
