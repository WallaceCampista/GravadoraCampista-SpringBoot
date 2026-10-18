package com.gravadoracampista.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MusicaRequestDto {

  @NotNull(message = "Preenchimento Obrigatório.")
  private Long idAlbum;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  @Size(max = 255)
  private String nomeMusica;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  @Size(max = 1000)
  private String resumoMusica;

  //OPCIONAL: QUANDO A MUSICA TEM MP3, A DURACAO E LIDA DO ARQUIVO E ESTE VALOR E IGNORADO
  @PositiveOrZero(message = "A duração não pode ser negativa.")
  private Double duracao;
}