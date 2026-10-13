package dev.raffadevs.licitacoes.avaliacao;

import dev.raffadevs.licitacoes.avaliacao.dto.AvaliacaoResponse;
import dev.raffadevs.licitacoes.avaliacao.dto.CasoAvaliacaoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documentos/{documentoId}/avaliacoes")
@Tag(name = "Avaliação", description = "Conjunto de perguntas com resposta esperada, para medir a qualidade das respostas")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    @Operation(summary = "Executa um conjunto de casos de teste contra o documento e registra o resultado")
    public ResponseEntity<List<AvaliacaoResponse>> executar(@PathVariable Long documentoId,
                                                              @Valid @RequestBody CasoAvaliacaoRequest request) {
        return ResponseEntity.ok(avaliacaoService.executar(documentoId, request));
    }

    @GetMapping
    @Operation(summary = "Histórico de avaliações já executadas para o documento")
    public ResponseEntity<List<AvaliacaoResponse>> listar(@PathVariable Long documentoId) {
        return ResponseEntity.ok(avaliacaoService.listar(documentoId));
    }
}
