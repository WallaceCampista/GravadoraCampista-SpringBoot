package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Musica_Table;
import com.gravadoracampista.model.entities.Musica;
import com.gravadoracampista.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoMusicaRepository extends JpaRepository<Avaliacao_Musica_Table, Long> {
  List<Avaliacao_Musica_Table> findByMusicaID(Musica musica);
  Optional<Avaliacao_Musica_Table> findByMusicaIDAndUsuario(Musica musica, Usuario usuario);
}
