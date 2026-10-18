package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.AvaliacaoRequestDto;
import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Album_Table;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Banda_Table;
import com.gravadoracampista.model.entities.Avaliacao.Avaliacao_Musica_Table;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.model.entities.Musica;
import com.gravadoracampista.model.entities.Usuario;
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


  //UMA NOTA POR USUARIO: AVALIAR DE NOVO SUBSTITUI A NOTA ANTERIOR DESSE USUARIO
  @Transactional(rollbackFor = Exception.class)
  public double avaliarBanda(Long id, AvaliacaoRequestDto request, Usuario usuario) {
    try {
      Banda banda = bandaService.getBandaById(id);

      Avaliacao_Banda_Table avaliacao = avaliacaoBandaRepository.findByBandaIDAndUsuario(banda, usuario)
              .orElseGet(Avaliacao_Banda_Table::new);
      avaliacao.setBandaID(banda);
      avaliacao.setUsuario(usuario);
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
  public double avaliarAlbum(Long id, AvaliacaoRequestDto request, Usuario usuario) {
    try {
      Album album = albumService.getAlbumById(id);

      Avaliacao_Album_Table avaliacao = avaliacaoAlbumRepository.findByAlbumIDAndUsuario(album, usuario)
              .orElseGet(Avaliacao_Album_Table::new);
      avaliacao.setAlbumID(album);
      avaliacao.setUsuario(usuario);
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
  public double avaliarMusica(Long id, AvaliacaoRequestDto request, Usuario usuario) {
    try {
      Musica musica = musicaService.getMusicaById(id);

      Avaliacao_Musica_Table avaliacao = avaliacaoMusicaRepository.findByMusicaIDAndUsuario(musica, usuario)
              .orElseGet(Avaliacao_Musica_Table::new);
      avaliacao.setMusicaID(musica);
      avaliacao.setUsuario(usuario);
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

  //NOTA QUE O USUARIO JA DEU (OU null SE AINDA NAO AVALIOU), PARA A TELA MOSTRAR "SUA NOTA"
  @Transactional(rollbackFor = Exception.class)
  public Double minhaNotaBanda(Long id, Usuario usuario) {
    Banda banda = bandaService.getBandaById(id);
    return avaliacaoBandaRepository.findByBandaIDAndUsuario(banda, usuario).map(Avaliacao_Banda_Table::getNota).orElse(null);
  }

  @Transactional(rollbackFor = Exception.class)
  public Double minhaNotaAlbum(Long id, Usuario usuario) {
    Album album = albumService.getAlbumById(id);
    return avaliacaoAlbumRepository.findByAlbumIDAndUsuario(album, usuario).map(Avaliacao_Album_Table::getNota).orElse(null);
  }

  @Transactional(rollbackFor = Exception.class)
  public Double minhaNotaMusica(Long id, Usuario usuario) {
    Musica musica = musicaService.getMusicaById(id);
    return avaliacaoMusicaRepository.findByMusicaIDAndUsuario(musica, usuario).map(Avaliacao_Musica_Table::getNota).orElse(null);
  }
}