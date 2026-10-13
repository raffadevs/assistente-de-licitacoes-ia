package dev.raffadevs.licitacoes.conversa.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.raffadevs.licitacoes.conversa.Mensagem;
import dev.raffadevs.licitacoes.conversa.Papel;

import java.time.Instant;
import java.util.List;

public record MensagemResponse(Long id, Papel papel, String conteudo, List<Integer> paginasCitadas, Instant criadaEm) {

    public static MensagemResponse from(Mensagem mensagem, ObjectMapper objectMapper) {
        List<Integer> paginas = List.of();
        if (mensagem.getTrechosCitados() != null) {
            try {
                paginas = objectMapper.readValue(mensagem.getTrechosCitados(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class));
            } catch (Exception ignored) {
                // trechosCitados mal formado não deve derrubar a listagem da conversa
            }
        }
        return new MensagemResponse(mensagem.getId(), mensagem.getPapel(), mensagem.getConteudo(), paginas,
                mensagem.getCriadaEm());
    }
}
