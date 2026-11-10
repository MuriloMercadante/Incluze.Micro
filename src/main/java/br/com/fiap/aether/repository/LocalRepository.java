package br.com.fiap.aether.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.fiap.aether.model.Local;

@Repository
public interface LocalRepository extends JpaRepository<Local, Long> {
}
