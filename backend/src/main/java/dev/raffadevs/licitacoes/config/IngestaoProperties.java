package dev.raffadevs.licitacoes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ingestao")
public class IngestaoProperties {

    private int tamanhoTrecho = 1200;
    private int sobreposicaoTrecho = 150;
    private int caracteresMinimosPorPagina = 20;

    public int getTamanhoTrecho() {
        return tamanhoTrecho;
    }

    public void setTamanhoTrecho(int tamanhoTrecho) {
        this.tamanhoTrecho = tamanhoTrecho;
    }

    public int getSobreposicaoTrecho() {
        return sobreposicaoTrecho;
    }

    public void setSobreposicaoTrecho(int sobreposicaoTrecho) {
        this.sobreposicaoTrecho = sobreposicaoTrecho;
    }

    public int getCaracteresMinimosPorPagina() {
        return caracteresMinimosPorPagina;
    }

    public void setCaracteresMinimosPorPagina(int caracteresMinimosPorPagina) {
        this.caracteresMinimosPorPagina = caracteresMinimosPorPagina;
    }
}
