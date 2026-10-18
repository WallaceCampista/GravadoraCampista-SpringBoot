package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Musica_Table;
import com.gravadoracampista.model.entities.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AvaliacaoMusicaRepository extends JpaRepository<Avaliacao_Musica_Table, Long> {
  List<Avaliacao_Musica_Table> findByMusicaID(Musica musica);
}