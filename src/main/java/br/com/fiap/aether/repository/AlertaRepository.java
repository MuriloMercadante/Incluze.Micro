package br.com.fiap.aether.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.aether.model.Alerta;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    boolean existsByLocalId(Long localId);
}
