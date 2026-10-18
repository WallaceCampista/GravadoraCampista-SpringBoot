package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.AlbumRequestDto;
import com.gravadoracampista.dtos.response.AlbumResponseDto;
import com.gravadoracampista.dtos.response.AlbumResponseFullDto;
import com.gravadoracampista.exceptions.ResourceNotFoundException;
import com.gravadoracampista.model.entities.Album;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.repository.AlbumRepository;
import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {

  private final BandaService bandaService;
  private final AlbumRepository albumRepository;

  @Transactional(rollbackFor = Exception.class)
  public AlbumRequestDto registroDeAlbum(AlbumRequestDto albumRequest) {
    // Verificar se a banda existe
    Banda banda = bandaService.getBandaById(albumRequest.getIdBanda());

    //CRIANDO LISTA DE POSSIVEIS ERROS
    List<String> errorMessages = new ArrayList<>();

    //VALIDACOES DE REGRAS DE NEGOCIO
    Optional<Album> existingAlbum= this.albumRepository.findByNomeAlbumAndExcluidoFalse(albumRequest.getNomeAlbum());
    if (existingAlbum.isPresent()) {
      errorMessages.add("Esse album já existe na gravadora.");
    }

    //VERIFICANDO SE A LISTA DE ERRO ESTA VAZIA. CASO CONTRARIO, RETORNA UMA EXCEÇÃO
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }

    // Criar o álbum
    Album album = new Album();
    album.setNomeAlbum(albumRequest.getNomeAlbum());
    album.setResumoAlbum(albumRequest.getResumoAlbum());
    album.setBanda(banda);

    // Salvar o álbum
    albumRepository.save(album);

    return albumRequest;
  }

  @Transactional(rollbackFor = Exception.class)
  public AlbumRequestDto updateAlbum(Long id, AlbumRequestDto request)
          throws ResourceNotFoundException, CreateEntitiesException {
    Album album = getAlbumById(id);
    Banda banda = bandaService.getBandaById(request.getIdBanda());

    //CRIANDO LISTA DE POSSIVEIS ERROS
    List<String> errorMessages = new ArrayList<>();

    //VALIDACOES DE REGRAS DE NEGOCIO
    Optional<Album> existingAlbum = this.albumRepository.findByNomeAlbumAndExcluidoFalse(request.getNomeAlbum());
    if (existingAlbum.isPresent() && !existingAlbum.get().getAlbumId().equals(album.getAlbumId())) {
      errorMessages.add("Esse album já existe na gravadora.");
    }

    //VERIFICANDO SE A LISTA DE ERRO ESTA VAZIA. CASO CONTRARIO, RETORNA UMA EXCEÇÃO
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }

    album.setBanda(banda);
    album.setNomeAlbum(request.getNomeAlbum());
    album.setResumoAlbum(request.getResumoAlbum());

    albumRepository.save(album);

    return request;
  }

  @Transactional(rollbackFor = Exception.class)
  public Page<AlbumResponseFullDto> searchAlbum(String nome, Pageable paginacao) {
    return albumRepository.findByNomeAlbumContainingAndExcluidoFalse(nome, paginacao)
            .map(album -> {
              AlbumResponseFullDto albumResponse = AlbumResponseFullDto.fromAlbum(album);
              albumResponse.setMusicas(album.getMusicas().stream()
                      .filter(musica -> !musica.getExcluido())
                      .collect(Collectors.toList()));
              return albumResponse;
            });
  }

  @Transactional(rollbackFor = Exception.class)
  public List<AlbumResponseDto> getAllAlbumSimples() {
    List<Album> albuns = this.albumRepository.findByExcluidoFalse();

    List<AlbumResponseDto> albunsResponse = new ArrayList<>();

    for (Album album : albuns) {
      AlbumResponseDto albumResponse = new AlbumResponseDto();
      albumResponse.setIdAlbum(album.getAlbumId());
      albumResponse.setNomeAlbum(album.getNomeAlbum());
      albumResponse.setResumoAlbum(album.getResumoAlbum());

      albunsResponse.add(albumResponse);
    }

    if(albunsResponse.isEmpty()){
      throw new ResourceNotFoundException("Nenhum album encontrada.");
    }

    return albunsResponse;
  }

  @Transactional(rollbackFor = Exception.class)
public List<AlbumResponseFullDto> getAllAlbumCompletos() {
    List<Album> albuns = this.albumRepository.findByExcluidoFalse();

    List<AlbumResponseFullDto> albunsResponse = new ArrayList<>();

    for (Album album : albuns) {
        AlbumResponseFullDto albumResponse = AlbumResponseFullDto.fromAlbum(album);

        // Filter out excluded music tracks
        albumResponse.setMusicas(album.getMusicas().stream()
            .filter(musica -> !musica.getExcluido())
            .collect(Collectors.toList()));

        albumResponse.setAvaliacaoMedia(album.getAvaliacaoMedia());
        albumResponse.setResumoAlbum(album.getResumoAlbum());
        albumResponse.setNomeAlbum(album.getNomeAlbum());
        albumResponse.setIdAlbum(album.getAlbumId());

        albunsResponse.add(albumResponse);
    }

    if (albunsResponse.isEmpty()) {
        throw new ResourceNotFoundException("Nenhum album encontrado.");
    }

    return albunsResponse;
}

  @Transactional(rollbackFor = Exception.class)
  public Album getAlbumById(Long id) {
    String msg = "Album não encontrado.";
    Album album = this.albumRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(msg));
    if (album.getExcluido()) {
      throw new ResourceNotFoundException(msg);
    }
    return album;
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteAlbum(Long id) {
    Album album = getAlbumById(id);
    album.setExcluido(true);

    // Excluir todas as músicas do álbum
    album.getMusicas().forEach(musica -> musica.setExcluido(true));

    this.albumRepository.save(album);
  }
}