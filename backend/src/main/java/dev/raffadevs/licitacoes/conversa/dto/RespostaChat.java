package dev.raffadevs.licitacoes.conversa.dto;

import java.util.List;

public record RespostaChat(Long conversaId, String resposta, List<Integer> paginasCitadas, boolean semResposta) {
}
