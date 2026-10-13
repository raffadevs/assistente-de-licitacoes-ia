package dev.raffadevs.licitacoes.avaliacao.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CasoAvaliacaoRequest(
        @NotEmpty(message = "Informe ao menos um caso de teste")
        @Valid List<Caso> casos
) {
    public record Caso(
            @NotBlank(message = "A pergunta é obrigatória") String pergunta,
            @NotBlank(message = "A resposta esperada é obrigatória") String respostaEsperada
    ) {
    }
}
