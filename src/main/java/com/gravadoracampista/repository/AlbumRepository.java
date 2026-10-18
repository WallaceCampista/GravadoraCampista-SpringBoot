package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Album;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

  Optional<Album> findByNomeAlbumAndExcluidoFalse(String nomeAlbum);
  List<Album> findByExcluidoFalse();
  Page<Album> findByNomeAlbumContainingAndExcluidoFalse(String nome, Pageable pageable);
}