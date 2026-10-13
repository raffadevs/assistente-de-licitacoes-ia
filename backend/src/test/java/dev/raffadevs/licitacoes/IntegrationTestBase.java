package dev.raffadevs.licitacoes;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Containers compartilhados entre todas as subclasses de teste de integração
 * (padrão "singleton container" — ver os demais projetos do portfólio).
 *
 * Usa um Ollama REAL rodando em container (Testcontainers tem um módulo
 * dedicado), com um modelo de chat minúsculo e o modelo de embedding padrão
 * pré-baixados ANTES do contexto Spring subir — assim o boot da aplicação
 * nos testes não fica esperando um download de modelo na primeira chamada.
 * Isso prova o pipeline de RAG ponta a ponta contra um modelo de verdade, não
 * um mock, ao custo de alguns minutos de pull de imagem/modelo na primeira
 * execução em cada ambiente (cacheado pelo Docker depois disso).
 */
@Import(IntegrationTestBase.TestRestTemplateConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class IntegrationTestBase {

    protected static final String MODELO_CHAT_TESTE = "qwen2:0.5b";
    protected static final String MODELO_EMBEDDING_TESTE = "nomic-embed-text";

    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
            DockerImageName.parse("pgvector/pgvector:pg16").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("licitacoes")
            .withUsername("licitacoes")
            .withPassword("licitacoes");

    static final OllamaContainer OLLAMA = new OllamaContainer("ollama/ollama:0.3.13");

    static {
        POSTGRES.start();
        OLLAMA.start();
        try {
            OLLAMA.execInContainer("ollama", "pull", MODELO_EMBEDDING_TESTE);
            OLLAMA.execInContainer("ollama", "pull", MODELO_CHAT_TESTE);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao baixar os modelos de teste no container Ollama", e);
        }
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.ai.ollama.base-url", OLLAMA::getEndpoint);
        registry.add("spring.ai.ollama.chat.model", () -> MODELO_CHAT_TESTE);
        registry.add("spring.ai.ollama.embedding.model", () -> MODELO_EMBEDDING_TESTE);
        // os modelos já foram baixados explicitamente acima; nenhuma tentativa
        // de pull deve acontecer durante o boot do contexto Spring
        registry.add("spring.ai.ollama.init.pull-model-strategy", () -> "never");
    }

    @TestConfiguration
    static class TestRestTemplateConfig {

        @Bean
        RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder()
                    .requestFactory(() -> new HttpComponentsClientHttpRequestFactory(HttpClients.createDefault()));
        }
    }
}
