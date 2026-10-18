package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Album_Table;
import com.gravadoracampista.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoAlbumRepository extends JpaRepository<Avaliacao_Album_Table, Long> {
  List<Avaliacao_Album_Table> findByAlbumID(Album album);
  Optional<Avaliacao_Album_Table> findByAlbumIDAndUsuario(Album album, Usuario usuario);
}
