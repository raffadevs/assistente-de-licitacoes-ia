package dev.raffadevs.licitacoes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;

import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    /**
     * A ingestão de um edital (extrair texto, gerar embeddings de dezenas de
     * trechos, chamar o modelo para o resumo estruturado) é dominada por
     * espera de I/O — candidata natural a virtual threads: cada upload gira
     * sua própria thread barata, sem competir por um pool fixo pequeno.
     */
    @Bean
    public AsyncTaskExecutor ingestaoExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
