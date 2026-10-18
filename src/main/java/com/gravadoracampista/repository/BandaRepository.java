package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Banda;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandaRepository extends JpaRepository<Banda, Long> {

  Optional<Banda> findByNomeBandaAndExcluidoFalse(String nomeBanda);
  List<Banda> findByExcluidoFalse();
  Page<Banda> findByNomeBandaContainingAndExcluidoFalse(String nome, Pageable paginacao);
}