package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.AvaliacaoRequestDto;
import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Album_Table;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Banda_Table;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Musica_Table;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.model.entities.Musica;
import com.gravadoracampista.repository.*;
import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

  private final BandaService bandaService;
  private final AlbumService albumService;
  private final MusicaService musicaService;

  private final BandaRepository bandaRepository;
  private final AlbumRepository albumRepository;
  private final MusicaRepository musicaRepository;
  private final AvaliacaoBandaRepository avaliacaoBandaRepository;
  private final AvaliacaoAlbumRepository avaliacaoAlbumRepository;
  private final AvaliacaoMusicaRepository avaliacaoMusicaRepository;


  @Transactional(rollbackFor = Exception.class)
  public double avaliarBanda(Long id, AvaliacaoRequestDto request) {
    try {
      Banda banda = bandaService.getBandaById(id);

      Avaliacao_Banda_Table avaliacao = new Avaliacao_Banda_Table();
      avaliacao.setBandaID(banda);
      if(request.getNota() > 10 || request.getNota() < 0){
        throw new CreateEntitiesException(List.of("Nota inválida"));
      }
      avaliacao.setNota(request.getNota());
      avaliacaoBandaRepository.save(avaliacao);

      List<Avaliacao_Banda_Table> avaliacoes = avaliacaoBandaRepository.findByBandaID(banda);
      double media = avaliacoes.stream().mapToDouble(Avaliacao_Banda_Table::getNota).average().orElse(0.0);

      banda.setAvaliacaoMedia(media);
      bandaRepository.save(banda);

      return media;
    } catch (Exception e) {
      throw new CreateEntitiesException(List.of("Não foi possível criar a avaliação da banda", e.getMessage()));
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public double avaliarAlbum(Long id, AvaliacaoRequestDto request) {
    try {
      Album album = albumService.getAlbumById(id);

      Avaliacao_Album_Table avaliacao = new Avaliacao_Album_Table();
      avaliacao.setAlbumID(album);
      if(request.getNota() > 10 || request.getNota() < 0){
        throw new CreateEntitiesException(List.of("Nota inválida"));
      }
      avaliacao.setNota(request.getNota());
      avaliacaoAlbumRepository.save(avaliacao);

      List<Avaliacao_Album_Table> avaliacoes = avaliacaoAlbumRepository.findByAlbumID(album);
      double media = avaliacoes.stream().mapToDouble(Avaliacao_Album_Table::getNota).average().orElse(0.0);

      album.setAvaliacaoMedia(media);
      albumRepository.save(album);

      return media;
    } catch (Exception e) {
      throw new CreateEntitiesException(List.of("Não foi possível criar a avaliação do album", e.getMessage()));
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public double avaliarMusica(Long id, AvaliacaoRequestDto request) {
    try {
      Musica musica = musicaService.getMusicaById(id);

      Avaliacao_Musica_Table avaliacao = new Avaliacao_Musica_Table();
      avaliacao.setMusicaID(musica);
      if(request.getNota() > 10 || request.getNota() < 0){
        throw new CreateEntitiesException(List.of("Nota inválida"));
      }
      avaliacao.setNota(request.getNota());
      avaliacaoMusicaRepository.save(avaliacao);

      List<Avaliacao_Musica_Table> avaliacoes = avaliacaoMusicaRepository.findByMusicaID(musica);
      double media = avaliacoes.stream().mapToDouble(Avaliacao_Musica_Table::getNota).average().orElse(0.0);

      musica.setAvaliacaoMedia(media);
      musicaRepository.save(musica);

      return media;
    } catch (Exception e) {
      throw new CreateEntitiesException(List.of("Não foi possível criar a avaliação da musica", e.getMessage()));
    }
  }
}