package com.gravadoracampista.infra.security.rotasAdministrativas;

import java.util.Arrays;
import java.util.List;

public class ListBandaController {

  //LISTA DE ROTAS EXCLUSIVAS DE USUARIOS ADMINS
  public static String getRotaBandaController() {
    List<String> ADMIN_ROUTES = Arrays.asList(
            "/banda/update/",
            "/banda/delete/"
    );
    return ADMIN_ROUTES.toString();
  }
}