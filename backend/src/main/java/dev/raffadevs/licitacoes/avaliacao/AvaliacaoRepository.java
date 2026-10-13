package dev.raffadevs.licitacoes.avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByDocumentoIdOrderByExecutadoEmDesc(Long documentoId);
}
