package com.gravadoracampista.controller;

import com.gravadoracampista.dtos.request.AvaliacaoRequestDto;
import com.gravadoracampista.dtos.request.BandaRequestDto;
import com.gravadoracampista.dtos.response.BandaResponseDto;
import com.gravadoracampista.dtos.response.BandaResponseFullDto;
import com.gravadoracampista.exceptions.ResourceNotFoundException;
import com.gravadoracampista.service.AvaliacaoService;
import com.gravadoracampista.service.BandaService;
import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/banda")
public class BandaController {

    private final BandaService bandaService;
    private final AvaliacaoService avaliacaoService;

    @PostMapping(value = "/novo-registro/")
    public ResponseEntity<String> registerBanda(@Valid @RequestBody BandaRequestDto request) {
        try {
            BandaRequestDto bandaDto = this.bandaService.registroDeBanda(request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(bandaDto.getClass()).toUri();
            return ResponseEntity.created(uri).body("Banda " + request.getNomeBanda() + " criada com sucesso!");
        } catch (CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}/")
    public ResponseEntity<String> updateAlbum(@PathVariable("id") Long id, @Valid @RequestBody BandaRequestDto request) {
        try {
            BandaRequestDto bandaDto = this.bandaService.updateBanda(id, request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(bandaDto.getClass()).toUri();
            return ResponseEntity.created(uri).body("Banda " + request.getNomeBanda() + " atualizada com sucesso!");
        } catch (ResourceNotFoundException | CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/avaliar-banda/{id}/")
    public ResponseEntity<String> avaliarBanda(@PathVariable("id") Long id, @Valid @RequestBody AvaliacaoRequestDto request) {
        try {
            double result = this.avaliacaoService.avaliarBanda(id, request);
            return ResponseEntity.ok("Nova média para banda: " +result);
        } catch (CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar-bandas-simples/")
    public ResponseEntity<Object> listarBandasSimples() {
        try {
            List<BandaResponseDto> bandas = this.bandaService.getAllBandaSimples();
            return ResponseEntity.ok().body(bandas);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar-bandas-completos/")
    public ResponseEntity<Object> listarBandasCompletos() {
        try {
            List<BandaResponseFullDto> bandas = bandaService.getAllBandaCompleta();
            return ResponseEntity.ok().body(bandas);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{nome}/")
    public ResponseEntity<Page<BandaResponseFullDto>> searchBanda(@PathVariable String nome, @PageableDefault(size = 20, page = 0) Pageable paginacao) {
        Page<BandaResponseFullDto> pesquisaBanda = bandaService.searchBanda(nome, paginacao);
        return ResponseEntity.ok().body(pesquisaBanda);
    }

    @DeleteMapping("/delete/{id}/")
    public ResponseEntity<String> deleteBanda(@PathVariable("id") Long id) {
        try {
            bandaService.deleteBanda(id);
            return ResponseEntity.ok().body("Banda excluída com sucesso!");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}