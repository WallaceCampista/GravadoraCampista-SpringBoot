package com.gravadoracampista.infra;

import com.gravadoracampista.model.entities.Usuario;
import com.gravadoracampista.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CriaAdminInicial implements CommandLineRunner {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${api.admin.username}")
  private String username;

  @Value("${api.admin.password}")
  private String password;

  @Value("${api.admin.email}")
  private String email;

  //O CADASTRO SEMPRE CRIA USUARIO COMUM, ENTAO GARANTE QUE EXISTA AO MENOS UM ADMIN
  @Override
  public void run(String... args) {
    if (usuarioRepository.existsByIsAdminTrueAndExcluidoFalse()) {
      return;
    }

    Usuario admin = usuarioRepository.findByUsernameAndExcluidoFalse(username).orElseGet(() -> {
      Usuario novoAdmin = new Usuario();
      novoAdmin.setUsername(username);
      novoAdmin.setPassword(passwordEncoder.encode(password));
      novoAdmin.setEmail(email);
      novoAdmin.setPrimeiroNome("Administrador");
      novoAdmin.setSobrenome("Gravadora");
      novoAdmin.setExcluido(false);
      novoAdmin.setCriadoEm(LocalDateTime.now());
      return novoAdmin;
    });
    admin.setIsAdmin(true);
    usuarioRepository.save(admin);

    log.info("Nenhum admin encontrado. Usuário '{}' definido como admin.", username);
  }
}
