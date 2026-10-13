package dev.raffadevs.licitacoes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("assistente-de-licitacoes-ia API")
                .description("Leitura de editais com RAG, citação de página obrigatória e avaliação de qualidade")
                .version("v1"));
    }
}
