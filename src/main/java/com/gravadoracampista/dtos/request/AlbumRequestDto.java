package com.gravadoracampista.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumRequestDto {

  @NotNull(message = "Preenchimento Obrigatório.")
  private Long idBanda;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  @Size(min = 1, max = 50)
  private String nomeAlbum;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  @Size(min = 4, max = 1000)
  private String resumoAlbum;
}