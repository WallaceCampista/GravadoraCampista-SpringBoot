package com.gravadoracampista.infra.security.rotasAdministrativas;

import java.util.Arrays;
import java.util.List;

public class ListAlbumController {

  //LISTA DE ROTAS EXCLUSIVAS DE USUARIOS ADMINS
  public static String getRotaAlbumController() {
    List<String> ADMIN_ROUTES = Arrays.asList(
            "/album/update/",
            "/album/delete/"
    );
    return ADMIN_ROUTES.toString();
  }
}