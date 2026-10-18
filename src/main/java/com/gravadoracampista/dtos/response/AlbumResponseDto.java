package com.gravadoracampista.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumResponseDto {

  private Long idAlbum;
  private String nomeAlbum;
  private String resumoAlbum;
}