package com.gravadoracampista.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Album")
public class Album {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long albumId;

  @Column(name = "Nome_Album", unique = true)
  @Size(min = 4, max = 50)
  private String nomeAlbum;

  @Column(name = "Resumo_Album")
  @Size(min = 4, max = 1000)
  private String resumoAlbum;

  @Column(name = "Duracao_Total")
  private double duracaoTotal = 0.00;

  @Column(name = "Media_Album")
  private double avaliacaoMedia = 0.00;

  @Column(name= "excluido")
  @JsonIgnore
  private Boolean excluido = false;

  //RELACIONAMENTO
  @ManyToOne //Criando relação de muitos para um
  @JoinColumn(name = "FK_Banda_id", referencedColumnName = "bandaId", nullable = false)
  @JsonIgnore
  private Banda banda;

  @OneToMany(mappedBy = "albumID", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonIgnore
  private List<Musica> musicas;

  public void atualizarDuracaoTotal() {
    double duracaoTotal = this.getMusicas().stream()
            .filter(musica -> !musica.getExcluido())
            .mapToDouble(Musica::getDuracao)
            .sum();
    this.setDuracaoTotal(duracaoTotal);
  }
}