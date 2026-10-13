package dev.raffadevs.licitacoes.conversa;

import dev.raffadevs.licitacoes.conversa.dto.MensagemResponse;
import dev.raffadevs.licitacoes.conversa.dto.PerguntarRequest;
import dev.raffadevs.licitacoes.conversa.dto.RespostaChat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Conversas", description = "Perguntas e respostas sobre um edital, com citação de página")
public class ConversaController {

    private final ConversaService conversaService;

    public ConversaController(ConversaService conversaService) {
        this.conversaService = conversaService;
    }

    @PostMapping("/api/documentos/{documentoId}/conversas")
    @Operation(summary = "Inicia uma nova conversa sobre um documento")
    public ResponseEntity<Map<String, Long>> criar(@PathVariable Long documentoId) {
        Long conversaId = conversaService.criar(documentoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("conversaId", conversaId));
    }

    @PostMapping("/api/conversas/{conversaId}/perguntas")
    @Operation(summary = "Faz uma pergunta na conversa; a resposta cita as páginas do edital usadas")
    public ResponseEntity<RespostaChat> perguntar(@PathVariable Long conversaId,
                                                   @Valid @RequestBody PerguntarRequest request) {
        return ResponseEntity.ok(conversaService.perguntar(conversaId, request.pergunta()));
    }

    @GetMapping("/api/conversas/{conversaId}/mensagens")
    @Operation(summary = "Histórico de mensagens da conversa")
    public ResponseEntity<List<MensagemResponse>> listarMensagens(@PathVariable Long conversaId) {
        return ResponseEntity.ok(conversaService.listarMensagens(conversaId));
    }
}
