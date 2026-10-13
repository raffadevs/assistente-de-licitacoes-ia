package dev.raffadevs.licitacoes.ingestao;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PdfExtractorServiceTest {

    private final PdfExtractorService service = new PdfExtractorService();

    @Test
    void extraiOTextoDeCadaPaginaSeparadamente() throws Exception {
        byte[] pdf = gerarPdfComDuasPaginas("Texto da primeira pagina", "Texto da segunda pagina");

        List<PaginaTexto> paginas = service.extrairPorPagina(pdf);

        assertThat(paginas).hasSize(2);
        assertThat(paginas.get(0).numero()).isEqualTo(1);
        assertThat(paginas.get(0).texto()).contains("Texto da primeira pagina");
        assertThat(paginas.get(1).numero()).isEqualTo(2);
        assertThat(paginas.get(1).texto()).contains("Texto da segunda pagina");
    }

    private byte[] gerarPdfComDuasPaginas(String textoPagina1, String textoPagina2) throws Exception {
        try (PDDocument documento = new PDDocument()) {
            adicionarPagina(documento, textoPagina1);
            adicionarPagina(documento, textoPagina2);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            documento.save(out);
            return out.toByteArray();
        }
    }

    private void adicionarPagina(PDDocument documento, String texto) throws Exception {
        PDPage pagina = new PDPage();
        documento.addPage(pagina);
        try (PDPageContentStream content = new PDPageContentStream(documento, pagina)) {
            content.beginText();
            content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);
            content.newLineAtOffset(50, 700);
            content.showText(texto);
            content.endText();
        }
    }
}
