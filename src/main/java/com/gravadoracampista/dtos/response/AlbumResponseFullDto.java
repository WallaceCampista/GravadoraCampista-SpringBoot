package com.gravadoracampista.dtos.response;

import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.model.entities.Musica;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumResponseFullDto {

  private Long idAlbum;
  private String nomeAlbum;
  private String resumoAlbum;
  private double duracaoTotal;
  private double avaliacaoMedia;
  private List<Musica> musicas;
  private Banda banda;

  public static AlbumResponseFullDto fromAlbum(Album album) {
    return new AlbumResponseFullDto(album.getAlbumId(), album.getNomeAlbum(), album.getResumoAlbum(), album.getDuracaoTotal(), album.getAvaliacaoMedia(), album.getMusicas(), album.getBanda());
  }
}