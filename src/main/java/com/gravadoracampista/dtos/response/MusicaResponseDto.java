package com.gravadoracampista.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MusicaResponseDto {

  private Long idMusica;
  private String nomeMusica;
  private String resumoMusica;
  private double duracao;
}