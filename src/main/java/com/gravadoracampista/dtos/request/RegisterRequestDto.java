package com.gravadoracampista.dtos.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterRequestDto {

  @NotEmpty(message = "Preenchimento Obrigatório.")
  private String username;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  private String password;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  private String email;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  private String primeiroNome;

  @NotEmpty(message = "Preenchimento Obrigatório.")
  private String sobrenome;
}