package com.gravadoracampista.dtos.response;

import lombok.*;

//NOTA QUE O USUARIO LOGADO DEU AO ITEM; null QUANDO AINDA NAO AVALIOU
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MinhaAvaliacaoResponseDto {

  private Double nota;
}
