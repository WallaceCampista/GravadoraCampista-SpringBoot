package com.gravadoracampista.infra;

import com.gravadoracampista.service.MidiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class MidiaConfig implements WebMvcConfigurer {

  private final MidiaService midiaService;

  //SERVE OS ARQUIVOS ENVIADOS EM /midia/<arquivo> (ROTA PUBLICA: <img> NAO ENVIA O TOKEN)
  //CADA ENVIO GERA UM NOME NOVO, ENTAO O NAVEGADOR PODE GUARDAR O ARQUIVO EM CACHE SEM PRAZO
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/midia/**")
            .addResourceLocations(midiaService.getPasta().toUri().toString())
            .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
  }
}
