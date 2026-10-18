package com.gravadoracampista.infra.security.rotasAdministrativas;

import java.util.Arrays;
import java.util.List;

public class ListMusicaController {

  //LISTA DE ROTAS EXCLUSIVAS DE USUARIOS ADMINS
  public static String getRotaMusicaController() {
    List<String> ADMIN_ROUTES = Arrays.asList(
            "/musica/update/",
            "/musica/delete/"
    );
    return ADMIN_ROUTES.toString();
  }
}