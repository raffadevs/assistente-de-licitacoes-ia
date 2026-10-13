package dev.raffadevs.licitacoes.resumo;

import dev.raffadevs.licitacoes.resumo.dto.ResumoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documentos/{documentoId}/resumo")
@Tag(name = "Resumo", description = "Extração estruturada: objeto, valor, modalidade, prazos e habilitação")
public class ResumoController {

    private final ResumoService resumoService;

    public ResumoController(ResumoService resumoService) {
        this.resumoService = resumoService;
    }

    @PostMapping
    @Operation(summary = "Gera (ou regenera) o resumo estruturado do edital")
    public ResponseEntity<ResumoResponse> gerar(@PathVariable Long documentoId) {
        return ResponseEntity.ok(resumoService.gerar(documentoId));
    }

    @GetMapping
    @Operation(summary = "Consulta o último resumo gerado")
    public ResponseEntity<ResumoResponse> buscar(@PathVariable Long documentoId) {
        return ResponseEntity.ok(resumoService.buscar(documentoId));
    }
}
