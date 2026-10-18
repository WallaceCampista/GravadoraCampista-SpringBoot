package com.gravadoracampista.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BandaRequestDto {

  @NotEmpty(message = "Preenchimento Obrigatório.")
  @Size(min = 1, max = 50)
  private String nomeBanda;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  @Size(max = 1000, message = "O resumo da banda não pode exceder 1000 caracteres.")
//  @Size(min = 4, max = 1000)
  private String resumoBanda;
}