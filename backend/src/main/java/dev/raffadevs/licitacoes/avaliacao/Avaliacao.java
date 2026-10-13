package dev.raffadevs.licitacoes.avaliacao;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_id", nullable = false)
    private Long documentoId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String pergunta;

    @Column(name = "resposta_esperada", nullable = false, columnDefinition = "TEXT")
    private String respostaEsperada;

    @Column(name = "resposta_obtida", columnDefinition = "TEXT")
    private String respostaObtida;

    private Boolean passou;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(name = "executado_em", nullable = false)
    private Instant executadoEm;

    protected Avaliacao() {
    }

    public Avaliacao(Long documentoId, String pergunta, String respostaEsperada, String respostaObtida,
                      boolean passou, String modelo) {
        this.documentoId = documentoId;
        this.pergunta = pergunta;
        this.respostaEsperada = respostaEsperada;
        this.respostaObtida = respostaObtida;
        this.passou = passou;
        this.modelo = modelo;
        this.executadoEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentoId() {
        return documentoId;
    }

    public String getPergunta() {
        return pergunta;
    }

    public String getRespostaEsperada() {
        return respostaEsperada;
    }

    public String getRespostaObtida() {
        return respostaObtida;
    }

    public Boolean getPassou() {
        return passou;
    }

    public String getModelo() {
        return modelo;
    }

    public Instant getExecutadoEm() {
        return executadoEm;
    }
}
