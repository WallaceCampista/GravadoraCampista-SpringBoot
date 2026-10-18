package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Musica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {

  Optional<Musica> findByNomeMusicaAndExcluidoFalse(String nomeMusica);
  List<Musica> findByExcluidoFalse();
  Page<Musica> findByNomeMusicaContainingAndExcluidoFalse(String nome, Pageable pageable);
}