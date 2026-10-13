package dev.raffadevs.licitacoes.ingestao;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extrai o texto de cada página de um PDF separadamente — preservar o número
 * da página é o que permite, mais adiante, citar "página 12" numa resposta,
 * em vez de só devolver um bloco de texto sem origem rastreável.
 *
 * PDFs digitalizados (escaneados, sem camada de texto) produzem páginas
 * vazias aqui; OCR fica fora do escopo deste MVP (listado no roteiro).
 */
@Service
public class PdfExtractorService {

    public List<PaginaTexto> extrairPorPagina(byte[] conteudoPdf) throws IOException {
        List<PaginaTexto> paginas = new ArrayList<>();

        try (PDDocument documento = Loader.loadPDF(conteudoPdf)) {
            PDFTextStripper stripper = new PDFTextStripper();
            int totalPaginas = documento.getNumberOfPages();

            for (int numero = 1; numero <= totalPaginas; numero++) {
                stripper.setStartPage(numero);
                stripper.setEndPage(numero);
                String texto = stripper.getText(documento);
                paginas.add(new PaginaTexto(numero, texto == null ? "" : texto.trim()));
            }
        }

        return paginas;
    }
}
