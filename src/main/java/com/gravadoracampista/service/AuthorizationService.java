package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.LoginRequestDto;
import com.gravadoracampista.dtos.response.LoginResponseDto;
import com.gravadoracampista.infra.security.TokenService;
import com.gravadoracampista.model.entities.Usuario;
import com.gravadoracampista.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthorizationService implements UserDetailsService {

    @Value("${api.jwt.token.expiration}")
    private Integer expirationToken;

    @Value("${api.jwt.refresh-token.expiration}")
    private Integer expirationRefreshToken;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String idFuncional) throws UsernameNotFoundException {
        return this.usuarioService.findUsuarioByidFuncional(idFuncional);
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginResponseDto login (LoginRequestDto requestDto) {

        var usenamePassword = new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());

        var auth = this.authenticationManager.authenticate(usenamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal(), expirationToken);
        var refreshToken = tokenService.generateToken((Usuario) auth.getPrincipal(), expirationRefreshToken);

        ((Usuario) auth.getPrincipal()).setUltimoLogin(LocalDateTime.now());

        this.usuarioRepository.save(((Usuario) auth.getPrincipal()));

        return LoginResponseDto
                .builder()
                .token(token)
                .refresh(refreshToken)
                .primeiroNome(String.valueOf(((Usuario) auth.getPrincipal()).getPrimeiroNome()))
                .isAdmin(((Usuario) auth.getPrincipal()).getIsAdmin())
                .build();
    }
}