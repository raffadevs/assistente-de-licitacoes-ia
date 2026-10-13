package dev.raffadevs.licitacoes.resumo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Formato de saída estruturada pedido ao modelo (Spring AI serializa este
 * record num JSON Schema e instrui o modelo a respondê-lo, convertendo de
 * volta automaticamente via {@code ChatClient...call().entity(...)}).
 */
public record ResumoExtraido(
        String objeto,
        BigDecimal valorEstimado,
        String modalidade,
        List<String> prazos,
        List<String> documentosExigidos
) {
}
