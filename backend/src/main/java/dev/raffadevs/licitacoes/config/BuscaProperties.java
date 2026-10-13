package dev.raffadevs.licitacoes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.busca")
public class BuscaProperties {

    private int limiteTrechos = 6;
    private double similaridadeMinima = 0.55;

    public int getLimiteTrechos() {
        return limiteTrechos;
    }

    public void setLimiteTrechos(int limiteTrechos) {
        this.limiteTrechos = limiteTrechos;
    }

    public double getSimilaridadeMinima() {
        return similaridadeMinima;
    }

    public void setSimilaridadeMinima(double similaridadeMinima) {
        this.similaridadeMinima = similaridadeMinima;
    }
}
