package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Musica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Long> {

  //O NOME DA MUSICA SO PRECISA SER UNICO DENTRO DO ALBUM (DUAS BANDAS PODEM TER UMA "Intro")
  Optional<Musica> findByNomeMusicaAndAlbumIDAndExcluidoFalse(String nomeMusica, Album album);
  List<Musica> findByExcluidoFalse();
  Page<Musica> findByNomeMusicaContainingAndExcluidoFalse(String nome, Pageable pageable);
}