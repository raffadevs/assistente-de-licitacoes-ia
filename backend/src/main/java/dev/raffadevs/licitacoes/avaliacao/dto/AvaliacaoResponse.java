package dev.raffadevs.licitacoes.avaliacao.dto;

import dev.raffadevs.licitacoes.avaliacao.Avaliacao;

import java.time.Instant;

public record AvaliacaoResponse(
        Long id, String pergunta, String respostaEsperada, String respostaObtida,
        Boolean passou, String modelo, Instant executadoEm
) {
    public static AvaliacaoResponse from(Avaliacao avaliacao) {
        return new AvaliacaoResponse(avaliacao.getId(), avaliacao.getPergunta(), avaliacao.getRespostaEsperada(),
                avaliacao.getRespostaObtida(), avaliacao.getPassou(), avaliacao.getModelo(),
                avaliacao.getExecutadoEm());
    }
}
