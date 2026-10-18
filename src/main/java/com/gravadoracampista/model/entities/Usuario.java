package com.gravadoracampista.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Usuario")
public class Usuario implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long usuarioId;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "nome")
  private String primeiroNome;

  @Column(name = "sobrenome")
  private String sobrenome;

  @NotNull(message = "O campo 'isAdmin' é obrigatório")
  @Column(name = "isAdmin")
  private Boolean isAdmin;

  @Column(name = "excluido")
  private Boolean excluido;

  @Column(name = "criadoEm")
  @DateTimeFormat(pattern = "dd/MM/YYYY - HH:mm:ss")
  private LocalDateTime criadoEm;

  @Column(name = "ultimoLogin")
  @DateTimeFormat(pattern = "dd/MM/YYYY - HH:mm:ss")
  private LocalDateTime ultimoLogin;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (this.isAdmin)
      return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"), new SimpleGrantedAuthority("ROLE_USER"));

    else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getUsername() {
    return username;
  }
  
   @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String toString() {
    return "Usuario{" +
            "usuarioId=" + usuarioId +
            ", idFuncional='" + username + '\'' +
            ", firstName='" + primeiroNome + '\'' +
            ", last_name='" + sobrenome + '\'' +
            '}';
  }
}