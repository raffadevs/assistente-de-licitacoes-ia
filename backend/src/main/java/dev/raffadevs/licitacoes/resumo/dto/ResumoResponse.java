package dev.raffadevs.licitacoes.resumo.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.licitacoes.resumo.ResumoEdital;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ResumoResponse(
        Long documentoId, String objeto, BigDecimal valorEstimado, String modalidade,
        List<String> prazos, List<String> documentosExigidos, Instant atualizadoEm
) {
    public static ResumoResponse from(ResumoEdital resumo, ObjectMapper objectMapper) {
        return new ResumoResponse(resumo.getDocumentoId(), resumo.getObjeto(), resumo.getValorEstimado(),
                resumo.getModalidade(), lerLista(resumo.getPrazos(), objectMapper),
                lerLista(resumo.getDocumentosExigidos(), objectMapper), resumo.getAtualizadoEm());
    }

    private static List<String> lerLista(String json, ObjectMapper objectMapper) {
        if (json == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return List.of();
        }
    }
}
