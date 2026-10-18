package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.MusicaRequestDto;
import com.gravadoracampista.dtos.response.MusicaResponseDto;
import com.gravadoracampista.dtos.response.MusicaResponseFullDto;
import com.gravadoracampista.exceptions.ResourceNotFoundException;
import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Musica;
import com.gravadoracampista.repository.AlbumRepository;
import com.gravadoracampista.repository.MusicaRepository;
import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MusicaService {

  private final AlbumService albumService;

  private final MusicaRepository musicaRepository;
  private final AlbumRepository albumRepository;

  @Transactional(rollbackFor = Exception.class)
  public MusicaRequestDto registroDeMusica(MusicaRequestDto musicaRequest) {
    // Verificar se o álbum existe
    Album album = albumService.getAlbumById(musicaRequest.getIdAlbum());

    //CRIANDO LISTA DE POSSIVEIS ERROS
    List<String> errorMessages = new ArrayList<>();

    //VALIDACOES DE REGRAS DE NEGOCIO
    Optional<Musica> existingMusica= this.musicaRepository.findByNomeMusicaAndExcluidoFalse(musicaRequest.getNomeMusica());
    if (existingMusica.isPresent()) {
      errorMessages.add("Essa musica já existe na gravadora.");
    }

    //VERIFICANDO SE A LISTA DE ERRO ESTA VAZIA. CASO CONTRARIO, RETORNA UMA EXCEÇÃO
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }

    // Criar a música
    Musica musica = new Musica();
    musica.setNomeMusica(musicaRequest.getNomeMusica());
    musica.setResumoMusica(musicaRequest.getResumoMusica());
    musica.setDuracao(musicaRequest.getDuracao());
    musica.setAlbumID(album);
    musica.setBandaID(album.getBanda());

    // Salvar a música
    musicaRepository.save(musica);

    // Atualizar a duração total do álbum
    double duracaoTotal = album.getMusicas().stream().mapToDouble(Musica::getDuracao).sum();
    album.setDuracaoTotal(duracaoTotal);
    albumRepository.save(album);

    return musicaRequest;
  }

  @Transactional(rollbackFor = Exception.class)
  public MusicaRequestDto updateMusica(Long id, MusicaRequestDto request)
          throws ResourceNotFoundException, CreateEntitiesException {
    Musica musica = getMusicaById(id);
    Album album = albumService.getAlbumById(request.getIdAlbum());
    List<String> errorMessages = new ArrayList<>();
    Optional<Musica> existingMusica = this.musicaRepository.findByNomeMusicaAndExcluidoFalse(request.getNomeMusica());
    if (existingMusica.isPresent() && !existingMusica.get().getMusicaId().equals(id)) {
      errorMessages.add("Essa musica já existe na gravadora.");
    }
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }
    Album albumAnterior = musica.getAlbumID();
    musica.setNomeMusica(request.getNomeMusica());
    musica.setResumoMusica(request.getResumoMusica());
    musica.setDuracao(request.getDuracao());
    musica.setAlbumID(album);
    musica.setBandaID(album.getBanda());
    musicaRepository.save(musica);
    if (albumAnterior != null && !albumAnterior.equals(album)) {
      albumAnterior.atualizarDuracaoTotal();
      albumRepository.save(albumAnterior);
    }
    album.atualizarDuracaoTotal();
    albumRepository.save(album);
    return request;
  }

  @Transactional(rollbackFor = Exception.class)
  public Page<MusicaResponseFullDto> searchMusica(String nome, Pageable paginacao) {
    return musicaRepository.findByNomeMusicaContainingAndExcluidoFalse(nome, paginacao)
            .map(MusicaResponseFullDto::fromMusica);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<MusicaResponseDto> getAllMusicaSimples() {
    List<Musica> musicas = this.musicaRepository.findByExcluidoFalse();

    List<MusicaResponseDto> musicasResponse = new ArrayList<>();

    for (Musica musica : musicas) {
      MusicaResponseDto musicaResponse = new MusicaResponseDto();
      musicaResponse.setIdMusica(musica.getMusicaId())  ;
      musicaResponse.setNomeMusica(musica.getNomeMusica());
      musicaResponse.setResumoMusica(musica.getResumoMusica());
      musicaResponse.setDuracao(musica.getDuracao());

      musicasResponse.add(musicaResponse);
    }

    if(musicasResponse.isEmpty()){
      throw new ResourceNotFoundException("Nenhuma musica encontrada.");
    }

    return musicasResponse;
  }

  @Transactional(rollbackFor = Exception.class)
  public List<MusicaResponseFullDto> getAllMusicaCompleta() {
    List<Musica> musicas = this.musicaRepository.findByExcluidoFalse();

    List<MusicaResponseFullDto> musicasResponse = new ArrayList<>();

    for (Musica musica : musicas) {
      MusicaResponseFullDto musicaResponse = new MusicaResponseFullDto();
      musicaResponse.setIdMusica(musica.getMusicaId());
      musicaResponse.setNomeMusica(musica.getNomeMusica());
      musicaResponse.setResumoMusica(musica.getResumoMusica());
      musicaResponse.setDuracao(musica.getDuracao());
      musicaResponse.setAvaliacaoMedia(musica.getAvaliacaoMedia());
      musicaResponse.setBandaID(musica.getBandaID());
      musicaResponse.setAlbumID(musica.getAlbumID());

      musicasResponse.add(musicaResponse);
    }

    if(musicasResponse.isEmpty()){
      throw new ResourceNotFoundException("Nenhuma musica encontrada.");
    }

    return musicasResponse;
  }

  @Transactional(rollbackFor = Exception.class)
  public Musica getMusicaById(Long id) {
    String msg = "Musica não encontrada.";
    Musica musica = this.musicaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(msg));
    if (musica.getExcluido()) {
      throw new ResourceNotFoundException(msg);
    }
    return musica;
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteMusica(Long id) {
    Musica musica = getMusicaById(id);
    musica.setExcluido(true);
    this.musicaRepository.save(musica);

    Album album = albumService.getAlbumById(musica.getAlbumID().getAlbumId());
    album.atualizarDuracaoTotal();
  }
}