package com.gravadoracampista.dtos.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateUserRequestDto {

  private String username;
  private String password;
  private String email;
  private String primeiroNome;
  private String sobrenome;
  private Boolean isAdmin;
}