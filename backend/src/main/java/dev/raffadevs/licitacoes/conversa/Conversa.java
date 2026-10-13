package dev.raffadevs.licitacoes.conversa;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "conversa")
public class Conversa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "documento_id", nullable = false)
    private Long documentoId;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    protected Conversa() {
    }

    public Conversa(Long documentoId) {
        this.documentoId = documentoId;
        this.criadaEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentoId() {
        return documentoId;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
