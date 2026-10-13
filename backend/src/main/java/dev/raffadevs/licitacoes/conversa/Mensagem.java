package dev.raffadevs.licitacoes.conversa;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "mensagem")
public class Mensagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conversa_id", nullable = false)
    private Long conversaId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Papel papel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    /** JSON com as páginas citadas na resposta, ex.: "[3,7]" — null em mensagens do usuário. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "trechos_citados", columnDefinition = "jsonb")
    private String trechosCitados;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    protected Mensagem() {
    }

    public Mensagem(Long conversaId, Papel papel, String conteudo, String trechosCitadosJson) {
        this.conversaId = conversaId;
        this.papel = papel;
        this.conteudo = conteudo;
        this.trechosCitados = trechosCitadosJson;
        this.criadaEm = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Long getConversaId() {
        return conversaId;
    }

    public Papel getPapel() {
        return papel;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getTrechosCitados() {
        return trechosCitados;
    }

    public Instant getCriadaEm() {
        return criadaEm;
    }
}
