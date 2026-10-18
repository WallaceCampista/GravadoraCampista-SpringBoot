package com.gravadoracampista.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.gravadoracampista.exceptions.UnauthorizedException;
import com.gravadoracampista.service.UsuarioService;
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
    System.out.println("SecurityFilter Invocado");

    var token = this.recoverToken(request);

    try {
      if (token != null) {

        var idFuncional = tokenService.validateToken(token);

        if (idFuncional != null && !idFuncional.isEmpty()) {

          // decodifica o JWT para acessar as claims
          DecodedJWT jwt = JWT.decode(token);
          Boolean isAdmin = jwt.getClaim("isAdmin").asBoolean();

          // obtém a rota da solicitação já tratada
          String requestRota = trataCaminhoParaFiltro(request.getRequestURI());

          // verifica se a rota está na lista das restritas a administradores e se isAdm é false
          if (RotasAcessoAdminMaster.getRotaAcessoAdmin().contains(requestRota) && (isAdmin == null || !isAdmin)) {
            throw new UnauthorizedException("Usuário não possui permissão de ADMIN");
          }


          UserDetails user = usuarioService.findUsuarioByidFuncional(idFuncional);

          var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          throw new AccessDeniedException("Erro ao autenticar.");
        }
      }

      filterChain.doFilter(request, response);

    } catch (AccessDeniedException | UnauthorizedException ex) {
      handlerExceptionResolver.resolveException(request, response, null, ex);
    }
  }
  private String trataCaminhoParaFiltro(String rota) {
    if(rota .contains( "/banda/update/")){
      rota = rota.split("/update/")[0] + "/update/";
    }
    if(rota .contains( "/banda/delete/")){
      rota = rota.split("/delete/")[0] + "/delete/";
    }
    if(rota .contains( "/album/update/")){
      rota = rota.split("/update/")[0] + "/update/";
    }
    if(rota .contains( "/album/delete/")){
      rota = rota.split("/delete/")[0] + "/delete/";
    }
    if(rota .contains( "/musica/update/")){
      rota = rota.split("/update/")[0] + "/update/";
    }
    if(rota .contains( "/musica/delete/")){
      rota = rota.split("/delete/")[0] + "/delete/";
    }
    if(rota .contains( "/usuarios/update/")){
      rota = rota.split("/update/")[0] + "/update/";
    }
    if(rota .contains( "/usuarios/delete/")){
      rota = rota.split("/delete/")[0] + "/delete/";
    }
    return rota;
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");

    if (authHeader == null) return null;

    return authHeader.replace("Bearer ", "");
  }
}