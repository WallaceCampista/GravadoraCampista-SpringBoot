package com.gravadoracampista.infra.security.rotasAdministrativas;

import java.util.Arrays;
import java.util.List;

public class ListUserController {

  //LISTA DE ROTAS EXCLUSIVAS DE USUARIOS ADMINS
  public static String getRotaUserController() {
    List<String> ADMIN_ROUTES = Arrays.asList(
            "/usuarios/update/",
            "/usuarios/delete/"
    );
    return ADMIN_ROUTES.toString();
  }
}