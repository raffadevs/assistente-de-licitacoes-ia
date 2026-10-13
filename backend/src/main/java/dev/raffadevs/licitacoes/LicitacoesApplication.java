package dev.raffadevs.licitacoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class LicitacoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(LicitacoesApplication.class, args);
    }
}
