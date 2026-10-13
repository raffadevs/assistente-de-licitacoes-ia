package dev.raffadevs.licitacoes.ingestao;

import dev.raffadevs.licitacoes.config.IngestaoProperties;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ChunkingServiceTest {

    private IngestaoProperties propriedades(int tamanho, int sobreposicao, int minimoPorPagina) {
        IngestaoProperties p = new IngestaoProperties();
        p.setTamanhoTrecho(tamanho);
        p.setSobreposicaoTrecho(sobreposicao);
        p.setCaracteresMinimosPorPagina(minimoPorPagina);
        return p;
    }

    @Test
    void naoDivideTextoMenorQueOTamanhoDoTrecho() {
        ChunkingService service = new ChunkingService(propriedades(1000, 100, 5));
        List<TrechoBruto> trechos = service.dividir(List.of(new PaginaTexto(1, "texto curto de uma pagina")));

        assertThat(trechos).hasSize(1);
        assertThat(trechos.get(0).pagina()).isEqualTo(1);
    }

    @Test
    void divideTextoLongoEmMultiplosTrechosComSobreposicao() {
        ChunkingService service = new ChunkingService(propriedades(100, 20, 5));
        String textoLongo = "a".repeat(250);

        List<TrechoBruto> trechos = service.dividir(List.of(new PaginaTexto(3, textoLongo)));

        assertThat(trechos).hasSizeGreaterThan(1);
        assertThat(trechos).allMatch(t -> t.pagina() == 3);
        assertThat(trechos).allMatch(t -> t.texto().length() <= 100);
    }

    @Test
    void nuncaMisturaTextoDeDuasPaginasNoMesmoTrecho() {
        ChunkingService service = new ChunkingService(propriedades(1000, 100, 5));
        List<TrechoBruto> trechos = service.dividir(List.of(
                new PaginaTexto(1, "conteudo da primeira pagina"),
                new PaginaTexto(2, "conteudo da segunda pagina")
        ));

        assertThat(trechos).hasSize(2);
        assertThat(trechos.get(0).pagina()).isEqualTo(1);
        assertThat(trechos.get(1).pagina()).isEqualTo(2);
    }

    @Test
    void ignoraPaginasComMenosTextoQueOMinimoConfigurado() {
        ChunkingService service = new ChunkingService(propriedades(1000, 100, 50));
        List<TrechoBruto> trechos = service.dividir(List.of(
                new PaginaTexto(1, "12"), // página só com numeração, abaixo do mínimo
                new PaginaTexto(2, "conteudo de verdade com texto suficiente para passar do minimo configurado")
        ));

        assertThat(trechos).hasSize(1);
        assertThat(trechos.get(0).pagina()).isEqualTo(2);
    }
}
