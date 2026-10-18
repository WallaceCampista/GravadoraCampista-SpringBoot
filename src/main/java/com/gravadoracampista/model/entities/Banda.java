package com.gravadoracampista.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Banda")
public class Banda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bandaId;

  @Column(name = "Nome_Banda", unique = true)
  private String nomeBanda;

  @Column(name = "Resumo_Banda")
  private String resumoBanda;

  @Column(name = "Media_Banda")
  private double avaliacaoMedia = 0.00;

  @Column(name= "excluido")
  @JsonIgnore
  private Boolean excluido = false;

  //RELACIONAMENTO
  @OneToMany(mappedBy = "banda")
  @JsonIgnore
  private List<Album> albuns;
  }