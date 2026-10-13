package dev.raffadevs.licitacoes.avaliacao;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AvaliacaoCheckerTest {

    @Test
    void passaQuandoRespostaObtidaContemARespostaEsperada() {
        boolean passou = AvaliacaoChecker.passou(
                "pregão eletrônico",
                "A modalidade desta licitação é pregão eletrônico, conforme página 1 [p. 1].");

        assertThat(passou).isTrue();
    }

    @Test
    void ignoraDiferencasDeAcentuacaoECaixa() {
        boolean passou = AvaliacaoChecker.passou(
                "atestado de capacidade técnica",
                "É exigido ATESTADO DE CAPACIDADE TECNICA para habilitação.");

        assertThat(passou).isTrue();
    }

    @Test
    void ignoraPontuacaoNaComparacao() {
        // "é" normaliza para a palavra "e" (não desaparece) — por isso o texto
        // esperado aqui inclui o "e" para continuar sendo um substring válido
        // depois da normalização, isolando só a diferença de pontuação (vírgula).
        boolean passou = AvaliacaoChecker.passou(
                "prazo e de 5 dias",
                "O prazo é de 5 dias, úteis.");

        assertThat(passou).isTrue();
    }

    @Test
    void falhaQuandoRespostaObtidaNaoContemAEsperada() {
        boolean passou = AvaliacaoChecker.passou(
                "pregão eletrônico",
                "Não encontrei essa informação no edital.");

        assertThat(passou).isFalse();
    }

    @Test
    void falhaQuandoAlgumLadoENulo() {
        assertThat(AvaliacaoChecker.passou(null, "qualquer coisa")).isFalse();
        assertThat(AvaliacaoChecker.passou("qualquer coisa", null)).isFalse();
    }
}
