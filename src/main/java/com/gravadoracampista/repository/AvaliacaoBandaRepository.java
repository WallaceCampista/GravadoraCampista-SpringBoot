package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Banda_Table;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoBandaRepository extends JpaRepository<Avaliacao_Banda_Table, Long> {
  List<Avaliacao_Banda_Table> findByBandaID(Banda banda);
  Optional<Avaliacao_Banda_Table> findByBandaIDAndUsuario(Banda banda, Usuario usuario);
}
