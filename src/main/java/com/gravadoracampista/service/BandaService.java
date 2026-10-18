package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.BandaRequestDto;
import com.gravadoracampista.dtos.response.BandaResponseDto;
import com.gravadoracampista.dtos.response.BandaResponseFullDto;
import com.gravadoracampista.exceptions.ResourceNotFoundException;
import com.gravadoracampista.model.entities.Banda;
import com.gravadoracampista.repository.BandaRepository;
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
public class BandaService {

  private final BandaRepository bandaRepository;

  @Transactional(rollbackFor = Exception.class)
  public BandaRequestDto registroDeBanda(BandaRequestDto bandaRequest) {

    //CRIANDO LISTA DE POSSIVEIS ERROS
    List<String> errorMessages = new ArrayList<>();

    //VALIDACOES DE REGRAS DE NEGOCIO
    Optional<Banda> existingBanda = this.bandaRepository.findByNomeBandaAndExcluidoFalse(bandaRequest.getNomeBanda());
    if (existingBanda.isPresent()) {
      errorMessages.add("Essa banda já existe na gravadora.");
    }

    //VERIFICANDO SE A LISTA DE ERRO ESTA VAZIA. CASO CONTRARIO, RETORNA UMA EXCEÇÃO
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }

    //PERSISTINDO NO BANCO DE DADOS
    Banda banda = new Banda();
    banda.setNomeBanda(bandaRequest.getNomeBanda());
    banda.setResumoBanda(bandaRequest.getResumoBanda());

    bandaRepository.save(banda);

    return bandaRequest;
  }

  @Transactional(rollbackFor = Exception.class)
  public BandaRequestDto updateBanda(Long id, BandaRequestDto request)
          throws ResourceNotFoundException, CreateEntitiesException {
    Banda banda = getBandaById(id);


    //CRIANDO LISTA DE POSSIVEIS ERROS
    List<String> errorMessages = new ArrayList<>();

    //VALIDACOES DE REGRAS DE NEGOCIO
    Optional<Banda> existingBanda = this.bandaRepository.findByNomeBandaAndExcluidoFalse(request.getNomeBanda());
    if (existingBanda.isPresent() && !existingBanda.get().getBandaId().equals(banda.getBandaId())) {
      errorMessages.add("Essa banda já existe na gravadora.");
    }

    //VERIFICANDO SE A LISTA DE ERRO ESTA VAZIA. CASO CONTRARIO, RETORNA UMA EXCEÇÃO
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }

    banda.setNomeBanda(request.getNomeBanda());
    banda.setResumoBanda(request.getResumoBanda());

    bandaRepository.save(banda);

    return request;
  }

  @Transactional(rollbackFor = Exception.class)
  public Page<BandaResponseFullDto> searchBanda(String nome, Pageable paginacao) {
    return bandaRepository.findByNomeBandaContainingAndExcluidoFalse(nome, paginacao)
            .map(banda -> {
              BandaResponseFullDto bandaResponse = BandaResponseFullDto.fromBanda(banda);
              bandaResponse.setAlbuns(banda.getAlbuns().stream()
                      .filter(album -> !album.getExcluido())
                      .collect(Collectors.toList()));
              return bandaResponse;
            });
  }

  @Transactional(rollbackFor = Exception.class)
  public List<BandaResponseDto> getAllBandaSimples() {
    List<Banda> bandas = this.bandaRepository.findByExcluidoFalse();

    List<BandaResponseDto> bandasResponse = new ArrayList<>();

    for (Banda banda : bandas) {
      BandaResponseDto bandaResponse = new BandaResponseDto();
      bandaResponse.setIdBanda(banda.getBandaId());
      bandaResponse.setNomeBanda(banda.getNomeBanda());
      bandaResponse.setResumoBanda(banda.getResumoBanda());
      bandasResponse.add(bandaResponse);
    }

    if (bandasResponse.isEmpty()) {
      throw new ResourceNotFoundException("Nenhuma banda encontrada.");
    }

    return bandasResponse;
  }

@Transactional(rollbackFor = Exception.class)
public List<BandaResponseFullDto> getAllBandaCompleta() {
    List<Banda> bandas = this.bandaRepository.findByExcluidoFalse();

    List<BandaResponseFullDto> bandasResponse = new ArrayList<>();

    for (Banda banda : bandas) {
        BandaResponseFullDto bandaResponse = BandaResponseFullDto.fromBanda(banda);
        bandaResponse.setIdBanda(banda.getBandaId());
        bandaResponse.setNomeBanda(banda.getNomeBanda());
        bandaResponse.setResumoBanda(banda.getResumoBanda());
        bandaResponse.setAvaliacaoMedia(banda.getAvaliacaoMedia());

        // Filter out excluded albums
        bandaResponse.setAlbuns(banda.getAlbuns().stream()
            .filter(album -> !album.getExcluido())
            .collect(Collectors.toList()));

        bandasResponse.add(bandaResponse);
    }

    if (bandasResponse.isEmpty()) {
        throw new ResourceNotFoundException("Nenhuma banda encontrada.");
    }

    return bandasResponse;
}

  @Transactional(rollbackFor = Exception.class)
  public Banda getBandaById(Long id) {
    String msg = "Banda não encontrada.";
    Banda banda = this.bandaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(msg));
    if (banda.getExcluido()) {
      throw new ResourceNotFoundException(msg);
    }
    return banda;
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteBanda(Long id) {
    Banda banda = getBandaById(id);
    banda.setExcluido(true);

    // Excluir todos os álbuns da banda
    banda.getAlbuns().forEach(album -> {
      album.setExcluido(true);

      // Excluir todas as músicas do álbum
      album.getMusicas().forEach(musica -> musica.setExcluido(true));
    });

    this.bandaRepository.save(banda);
  }
}