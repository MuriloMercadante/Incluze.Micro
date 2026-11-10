package br.com.fiap.aether.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.fiap.aether.model.Avaliacao;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    boolean existsByLocalId(Long localId);

    boolean existsByUsuarioIdAndLocalId(Long usuarioId, Long localId);

    boolean existsByUsuarioIdAndLocalIdAndIdNot(Long usuarioId, Long localId, Long id);

    @Query("select avg(a.nota * 1.0) from Avaliacao a where a.local.id = :localId")
    Double calcularNotaMedia(@Param("localId") Long localId);
}
