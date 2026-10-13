package dev.raffadevs.licitacoes.avaliacao;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Verificação determinística (sem IA) de uma resposta obtida contra a
 * resposta esperada de um caso de avaliação: normaliza acentos, caixa e
 * pontuação dos dois lados e checa se o texto esperado aparece contido no
 * texto obtido.
 *
 * É deliberadamente simples — um heurístico de contenção textual, não um
 * "LLM-as-judge". Suficiente para pegar regressões óbvias (a resposta parou
 * de mencionar um dado que antes mencionava) sem o custo e a não-determinismo
 * de usar outro modelo para julgar a resposta. Um avaliador por LLM é uma
 * evolução natural, listada no roteiro.
 */
public final class AvaliacaoChecker {

    private static final Pattern NAO_ALFANUMERICO = Pattern.compile("[^\\p{L}\\p{N}\\s]");
    private static final Pattern ESPACOS_REPETIDOS = Pattern.compile("\\s+");

    private AvaliacaoChecker() {
    }

    public static boolean passou(String respostaEsperada, String respostaObtida) {
        if (respostaEsperada == null || respostaObtida == null) {
            return false;
        }
        return normalizar(respostaObtida).contains(normalizar(respostaEsperada));
    }

    private static String normalizar(String texto) {
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        String soAlfanumerico = NAO_ALFANUMERICO.matcher(semAcentos).replaceAll(" ");
        return ESPACOS_REPETIDOS.matcher(soAlfanumerico.toLowerCase().trim()).replaceAll(" ");
    }
}
