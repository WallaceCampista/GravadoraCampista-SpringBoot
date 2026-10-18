package com.gravadoracampista.infra.security;

import com.gravadoracampista.service.UsuarioService;
import com.gravadoracampista.service.exceptions.userExceptions.UsernameNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final HandlerExceptionResolver handlerExceptionResolver;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private UsuarioService usuarioService;


  @Autowired
  public SecurityFilter(HandlerExceptionResolver handlerExceptionResolver) {
    this.handlerExceptionResolver = handlerExceptionResolver;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    var token = this.recoverToken(request);

    try {
      if (token != null) {

        var idFuncional = tokenService.validateToken(token);

        if (idFuncional != null && !idFuncional.isEmpty()) {

          //O PERFIL (ADMIN OU NAO) VEM DO BANCO, NAO DO TOKEN: TIRAR O ADMIN DE ALGUEM VALE NA HORA
          UserDetails user = usuarioService.findUsuarioByidFuncional(idFuncional);

          var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          throw new AccessDeniedException("Erro ao autenticar.");
        }
      }

      filterChain.doFilter(request, response);

    } catch (AccessDeniedException | UsernameNotFoundException ex) {
      handlerExceptionResolver.resolveException(request, response, null, ex);
    }
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");

    if (authHeader == null) return null;

    return authHeader.replace("Bearer ", "");
  }
}