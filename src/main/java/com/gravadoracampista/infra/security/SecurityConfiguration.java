package com.gravadoracampista.infra.security;

import com.gravadoracampista.exceptions.UnauthorizedException;
import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] POST_ROLE_ALLPERMIT = {
            "/usuarios/post/login/",
            "/usuarios/post/registro/"

    };
    private static final String[] GET_ROLE_ALLPERMIT = {
            //IMAGENS E MP3 ENVIADOS: AS TAGS <img> E <audio> NAO ENVIAM O TOKEN
            "/midia/**"
    };

    //ROTAS SO DE ADMIN, POR METODO. QUEM DECIDE E O SPRING SECURITY, QUE LE A ROTA IGUAL AOS CONTROLLERS
    //(COMPARAR O TEXTO DA URL DEIXAVA PASSAR "/banda/%75pdate/1/", QUE O SPRING ENTENDE COMO "/banda/update/1/")
    private static final String[] PUT_ROLE_ADMIN = {
            "/banda/update/**",
            "/album/update/**",
            "/musica/update/**",
            "/usuarios/update/**"
    };
    private static final String[] DELETE_ROLE_ADMIN = {
            "/banda/delete/**",
            "/album/delete/**",
            "/musica/delete/**",
            "/usuarios/delete/**",
            "/banda/foto/**",
            "/album/capa/**",
            "/musica/audio/**"
    };
    private static final String[] POST_ROLE_ADMIN = {
            "/banda/foto/**",
            "/album/capa/**",
            "/musica/audio/**"
    };
    private static final String[] GET_ROLE_ADMIN = {
            "/usuarios/all"
    };

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Bean
    public SecurityFilter securityFilter() {
        return new SecurityFilter(exceptionResolver);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        return httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        //A RESPOSTA DE ERRO (404, 400...) PASSA DE NOVO PELA SEGURANCA; SEM ISSO TODO ERRO VIRAVA 403
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/usuarios/**").permitAll()
                        .requestMatchers(HttpMethod.POST, POST_ROLE_ALLPERMIT).permitAll()
                        .requestMatchers(HttpMethod.GET, GET_ROLE_ALLPERMIT).permitAll()
                        .requestMatchers(HttpMethod.PUT, PUT_ROLE_ADMIN).hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, DELETE_ROLE_ADMIN).hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, POST_ROLE_ADMIN).hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, GET_ROLE_ADMIN).hasRole("ADMINISTRADOR")
                        .anyRequest().authenticated()
                )
                //LOGADO SEM SER ADMIN: 401 COM MENSAGEM (O 403 FICA PARA TOKEN AUSENTE OU INVALIDO, QUE O FRONT TRATA COMO SESSAO EXPIRADA)
                .exceptionHandling(exception -> exception.accessDeniedHandler((request, response, accessDeniedException) ->
                        exceptionResolver.resolveException(request, response, null,
                                new UnauthorizedException("Usuário não possui permissão de ADMIN"))))
                .addFilterBefore(securityFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}