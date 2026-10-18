package com.gravadoracampista.dtos.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AvaliacaoRequestDto {

  @NotNull(message = "Preenchimento Obrigatório.")
  private double nota;
}