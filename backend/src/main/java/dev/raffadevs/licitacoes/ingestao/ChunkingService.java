package dev.raffadevs.licitacoes.ingestao;

import dev.raffadevs.licitacoes.config.IngestaoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Divide o texto de cada página em trechos de tamanho fixo com sobreposição,
 * SEM nunca misturar texto de páginas diferentes num mesmo trecho — cada
 * trecho carrega exatamente um número de página, para que a citação na
 * resposta final seja sempre exata.
 *
 * É uma função pura (sem I/O, sem estado), o que a torna trivial de testar:
 * mesmo texto de entrada, mesmo resultado sempre.
 */
@Service
@EnableConfigurationProperties(IngestaoProperties.class)
public class ChunkingService {

    private final IngestaoProperties properties;

    public ChunkingService(IngestaoProperties properties) {
        this.properties = properties;
    }

    public List<TrechoBruto> dividir(List<PaginaTexto> paginas) {
        List<TrechoBruto> trechos = new ArrayList<>();

        for (PaginaTexto pagina : paginas) {
            String texto = pagina.texto();
            if (texto.length() < properties.getCaracteresMinimosPorPagina()) {
                continue; // página em branco ou só com ruído (ex.: numeração solta)
            }

            int tamanho = properties.getTamanhoTrecho();
            int sobreposicao = properties.getSobreposicaoTrecho();
            int passo = Math.max(1, tamanho - sobreposicao);

            for (int inicio = 0; inicio < texto.length(); inicio += passo) {
                int fim = Math.min(inicio + tamanho, texto.length());
                trechos.add(new TrechoBruto(pagina.numero(), texto.substring(inicio, fim)));
                if (fim == texto.length()) {
                    break;
                }
            }
        }

        return trechos;
    }
}
