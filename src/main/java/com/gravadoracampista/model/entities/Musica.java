package com.gravadoracampista.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Musica")
public class Musica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long musicaId;

  @Column(name = "Nome_Musica")
  private String nomeMusica;

  @Column(name = "Resumo_Musica")
  private String resumoMusica;

  @Column(name = "Media_Musica")
  private double avaliacaoMedia = 0.00;

  @Column(name = "Duracao")
  private double duracao;

  @Column(name= "excluido")
  @JsonIgnore
  private Boolean excluido = false;

  //RELACIONAMENTO
  @ManyToOne
  @JoinColumn(name = "FK_Banda_id")
  @JsonIgnore
  private Banda bandaID;

  @ManyToOne
  @JoinColumn(name = "FK_Album_id")
  @JsonIgnore
  private Album albumID;
  }