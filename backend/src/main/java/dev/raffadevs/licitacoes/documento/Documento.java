package dev.raffadevs.licitacoes.documento;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 250)
    private String titulo;

    @Column(length = 250)
    private String orgao;

    @Column(name = "arquivo_nome", nullable = false, length = 255)
    private String arquivoNome;

    private Integer paginas;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_processamento", nullable = false, length = 20)
    private StatusProcessamento statusProcessamento;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    protected Documento() {
    }

    public Documento(String titulo, String orgao, String arquivoNome) {
        this.titulo = titulo;
        this.orgao = orgao;
        this.arquivoNome = arquivoNome;
        this.statusProcessamento = StatusProcessamento.PENDENTE;
        this.criadoEm = Instant.now();
    }

    public void marcarProcessando() {
        this.statusProcessamento = StatusProcessamento.PROCESSANDO;
    }

    public void marcarConcluido(int paginas) {
        this.statusProcessamento = StatusProcessamento.CONCLUIDO;
        this.paginas = paginas;
    }

    public void marcarFalhou(String motivo) {
        this.statusProcessamento = StatusProcessamento.FALHOU;
        this.observacoes = motivo;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getOrgao() {
        return orgao;
    }

    public String getArquivoNome() {
        return arquivoNome;
    }

    public Integer getPaginas() {
        return paginas;
    }

    public StatusProcessamento getStatusProcessamento() {
        return statusProcessamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }
}
