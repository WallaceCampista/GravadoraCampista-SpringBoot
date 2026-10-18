package com.gravadoracampista.dtos.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDto {

  private Long usuarioId;
  private String username;
  private String password;
  private String email;
  private String nome ;
  private String sobrenome;
  private Boolean isAdmin;
  private LocalDateTime criadoEm;
  private LocalDateTime ultimoLogin;
}