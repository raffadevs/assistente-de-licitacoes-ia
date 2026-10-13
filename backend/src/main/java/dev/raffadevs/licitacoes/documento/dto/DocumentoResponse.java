package dev.raffadevs.licitacoes.documento.dto;

import dev.raffadevs.licitacoes.documento.Documento;
import dev.raffadevs.licitacoes.documento.StatusProcessamento;

import java.time.Instant;

public record DocumentoResponse(
        Long id, String titulo, String orgao, String arquivoNome, Integer paginas,
        StatusProcessamento statusProcessamento, String observacoes, Instant criadoEm
) {
    public static DocumentoResponse from(Documento documento) {
        return new DocumentoResponse(documento.getId(), documento.getTitulo(), documento.getOrgao(),
                documento.getArquivoNome(), documento.getPaginas(), documento.getStatusProcessamento(),
                documento.getObservacoes(), documento.getCriadoEm());
    }
}
