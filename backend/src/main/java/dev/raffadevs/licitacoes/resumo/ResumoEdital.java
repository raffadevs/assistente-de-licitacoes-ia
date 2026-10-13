package dev.raffadevs.licitacoes.resumo;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "resumo_edital")
public class ResumoEdital {

    @Id
    @Column(name = "documento_id")
    private Long documentoId;

    @Column(columnDefinition = "TEXT")
    private String objeto;

    @Column(name = "valor_estimado", precision = 14, scale = 2)
    private BigDecimal valorEstimado;

    @Column(length = 80)
    private String modalidade;

    /** JSON com a lista de prazos em texto livre, ex.: ["Impugnação: até 3 dias úteis antes da sessão"]. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String prazos;

    /** JSON com a lista de documentos de habilitação exigidos. */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "documentos_exigidos", columnDefinition = "jsonb")
    private String documentosExigidos;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected ResumoEdital() {
    }

    public ResumoEdital(Long documentoId, String objeto, BigDecimal valorEstimado, String modalidade,
                         String prazosJson, String documentosExigidosJson) {
        this.documentoId = documentoId;
        atualizar(objeto, valorEstimado, modalidade, prazosJson, documentosExigidosJson);
    }

    public void atualizar(String objeto, BigDecimal valorEstimado, String modalidade, String prazosJson,
                           String documentosExigidosJson) {
        this.objeto = objeto;
        this.valorEstimado = valorEstimado;
        this.modalidade = modalidade;
        this.prazos = prazosJson;
        this.documentosExigidos = documentosExigidosJson;
        this.atualizadoEm = Instant.now();
    }

    public Long getDocumentoId() {
        return documentoId;
    }

    public String getObjeto() {
        return objeto;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public String getModalidade() {
        return modalidade;
    }

    public String getPrazos() {
        return prazos;
    }

    public String getDocumentosExigidos() {
        return documentosExigidos;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
