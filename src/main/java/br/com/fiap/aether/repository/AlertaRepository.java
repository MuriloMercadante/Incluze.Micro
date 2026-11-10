package br.com.fiap.aether.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.fiap.aether.model.Alerta;
import br.com.fiap.aether.model.TipoAlerta;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    boolean existsByLocalId(Long localId);

    /**
     * Busca de alertas com filtros opcionais. Um alerta está "vigente" quando
     * está marcado como ativo e a data de expiração ainda não passou.
     */
    @Query("""
            select a from Alerta a
            where (:localId is null or a.local.id = :localId)
              and (:tipo is null or a.tipo = :tipo)
              and (:vigente is null
                   or (:vigente = true and a.ativo = true and a.dataExpiracao > :agora)
                   or (:vigente = false and (a.ativo = false or a.dataExpiracao <= :agora)))
            order by a.dataCriacao desc
            """)
    List<Alerta> buscar(@Param("localId") Long localId,
            @Param("tipo") TipoAlerta tipo,
            @Param("vigente") Boolean vigente,
            @Param("agora") LocalDateTime agora);
}
