package com.gravadoracampista.dtos.response;

import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Banda;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BandaResponseFullDto {

  private Long idBanda;
  private String nomeBanda;
  private String resumoBanda;
  private double avaliacaoMedia;
  private List<Album> albuns;

  public static BandaResponseFullDto fromBanda(Banda banda) {
    return new BandaResponseFullDto(banda.getBandaId(), banda.getNomeBanda(), banda.getResumoBanda(), banda.getAvaliacaoMedia(), banda.getAlbuns());
  }
}