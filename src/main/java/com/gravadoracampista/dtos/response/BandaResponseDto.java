package com.gravadoracampista.dtos.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BandaResponseDto {

  private Long idBanda;
  private String nomeBanda;
  private String resumoBanda;
}