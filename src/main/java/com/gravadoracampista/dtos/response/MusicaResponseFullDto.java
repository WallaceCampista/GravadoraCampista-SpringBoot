package com.gravadoracampista.dtos.response;

import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.model.entities.Musica;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MusicaResponseFullDto {

  private Long idMusica;
  private String nomeMusica;
  private String resumoMusica;
  private double duracao;
  private double avaliacaoMedia;
  private Album albumID;
  private Banda bandaID;

  public static MusicaResponseFullDto fromMusica(Musica musica) {
    return new MusicaResponseFullDto(musica.getMusicaId(), musica.getNomeMusica(), musica.getResumoMusica(), musica.getDuracao(), musica.getAvaliacaoMedia(), musica.getAlbumID(), musica.getBandaID());
  }
}