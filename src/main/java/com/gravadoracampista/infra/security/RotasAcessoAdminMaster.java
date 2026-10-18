package com.gravadoracampista.infra.security;

import com.gravadoracampista.infra.security.rotasAdministrativas.ListBandaController;
import com.gravadoracampista.infra.security.rotasAdministrativas.ListAlbumController;
import com.gravadoracampista.infra.security.rotasAdministrativas.ListMusicaController;
import com.gravadoracampista.infra.security.rotasAdministrativas.ListUserController;

import java.util.Arrays;
import java.util.List;

public class RotasAcessoAdminMaster {
  public static String getRotaAcessoAdmin() {
    List<String> ADMIN_ROUTES = Arrays.asList(
            ListAlbumController.getRotaAlbumController(),
            ListMusicaController.getRotaMusicaController(),
            ListBandaController.getRotaBandaController(),
            ListUserController.getRotaUserController()
    );
    return ADMIN_ROUTES.toString();
  }
}