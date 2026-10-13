package dev.raffadevs.licitacoes.conversa.dto;

import jakarta.validation.constraints.NotBlank;

public record PerguntarRequest(@NotBlank(message = "A pergunta é obrigatória") String pergunta) {
}
