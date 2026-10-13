package dev.raffadevs.licitacoes.conversa;

import com.fasterxml.jackson.databind.JsonNode;
import dev.raffadevs.licitacoes.IntegrationTestBase;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Sobe um PDF de verdade, deixa o pipeline de ingestão rodar (extração,
 * chunking, embeddings via Ollama real, indexação no pgvector) e faz uma
 * pergunta cuja resposta só existe na página 2 — provando que a citação de
 * página do RAG é exata, não um palpite.
 */
class ConversaFluxoCompletoIT extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void perguntaSobreConteudoDaPagina2CitaAPagina2() {
        byte[] pdf = gerarEditalDeTeste();

        Long documentoId = enviarDocumento(pdf);
        aguardarProcessamentoConcluido(documentoId);

        Long conversaId = criarConversa(documentoId);

        ResponseEntity<JsonNode> resposta = restTemplate.postForEntity(
                "/api/conversas/" + conversaId + "/perguntas",
                jsonRequest("""
                        {"pergunta":"Qual é o prazo de impugnação deste edital?"}
                        """),
                JsonNode.class);

        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resposta.getBody().get("semResposta").asBoolean()).isFalse();

        boolean citouPagina2 = false;
        for (JsonNode pagina : resposta.getBody().get("paginasCitadas")) {
            if (pagina.asInt() == 2) {
                citouPagina2 = true;
            }
        }
        assertThat(citouPagina2).isTrue();
    }

    private Long enviarDocumento(byte[] pdf) {
        MultiValueMap<String, Object> partes = new LinkedMultiValueMap<>();
        partes.add("arquivo", new ByteArrayResource(pdf) {
            @Override
            public String getFilename() {
                return "edital-teste.pdf";
            }
        });
        partes.add("titulo", "Edital de teste");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(partes, headers);

        ResponseEntity<JsonNode> resposta = restTemplate.postForEntity("/api/documentos", request, JsonNode.class);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        return resposta.getBody().get("id").asLong();
    }

    private void aguardarProcessamentoConcluido(Long documentoId) {
        Awaitility.await().atMost(Duration.ofMinutes(2)).pollInterval(Duration.ofSeconds(2)).untilAsserted(() -> {
            ResponseEntity<JsonNode> resposta = restTemplate.getForEntity(
                    "/api/documentos/" + documentoId, JsonNode.class);
            String status = resposta.getBody().get("statusProcessamento").asText();
            assertThat(status).isIn("CONCLUIDO");
        });
    }

    private Long criarConversa(Long documentoId) {
        ResponseEntity<JsonNode> resposta = restTemplate.postForEntity(
                "/api/documentos/" + documentoId + "/conversas", null, JsonNode.class);
        assertThat(resposta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return resposta.getBody().get("conversaId").asLong();
    }

    private byte[] gerarEditalDeTeste() {
        try (PDDocument documento = new PDDocument()) {
            adicionarPagina(documento,
                    "EDITAL DE PREGAO ELETRONICO No 001/2026. Objeto: aquisicao de equipamentos de informatica.");
            adicionarPagina(documento,
                    "O prazo para impugnacao deste edital e de ate 3 (tres) dias uteis antes da data fixada "
                            + "para abertura da sessao publica, conforme artigo 24 da Lei 14133/2021.");
            adicionarPagina(documento,
                    "Documentos de habilitacao exigidos: certidao negativa de debitos, atestado de capacidade "
                            + "tecnica e balanco patrimonial do ultimo exercicio.");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            documento.save(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private void adicionarPagina(PDDocument documento, String texto) throws Exception {
        PDPage pagina = new PDPage();
        documento.addPage(pagina);
        try (PDPageContentStream content = new PDPageContentStream(documento, pagina)) {
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
            content.newLineAtOffset(50, 700);
            for (String linha : quebrarEmLinhas(texto, 85)) {
                content.showText(linha);
                content.newLineAtOffset(0, -15);
            }
            content.endText();
        }
    }

    private java.util.List<String> quebrarEmLinhas(String texto, int maxCaracteres) {
        java.util.List<String> linhas = new java.util.ArrayList<>();
        StringBuilder atual = new StringBuilder();
        for (String palavra : texto.split(" ")) {
            if (atual.length() + palavra.length() + 1 > maxCaracteres) {
                linhas.add(atual.toString());
                atual = new StringBuilder();
            }
            if (!atual.isEmpty()) {
                atual.append(' ');
            }
            atual.append(palavra);
        }
        if (!atual.isEmpty()) {
            linhas.add(atual.toString());
        }
        return linhas;
    }

    private HttpEntity<String> jsonRequest(String corpo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(corpo, headers);
    }
}
