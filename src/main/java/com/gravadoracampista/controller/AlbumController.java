package com.gravadoracampista.controller;

import com.gravadoracampista.dtos.request.AlbumRequestDto;
import com.gravadoracampista.dtos.request.AvaliacaoRequestDto;
import com.gravadoracampista.dtos.response.AlbumResponseDto;
import com.gravadoracampista.dtos.response.AlbumResponseFullDto;
import com.gravadoracampista.exceptions.ResourceNotFoundException;
import com.gravadoracampista.service.AlbumService;
import com.gravadoracampista.service.AvaliacaoService;
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
@RequestMapping("/album")
public class AlbumController {

    private final AlbumService albumService;
    private final AvaliacaoService avaliacaoService;

    @PostMapping(value = "/novo-registro/")
    public ResponseEntity<String> registerAlbum(@Valid @RequestBody AlbumRequestDto request) {
        try {
            AlbumRequestDto albumDto = this.albumService.registroDeAlbum(request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(albumDto.getClass()).toUri();
            return ResponseEntity.created(uri).body("Album " + request.getNomeAlbum() + " criado com sucesso!");
        } catch (ResourceNotFoundException | CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}/")
    public ResponseEntity<String> updateAlbum(@PathVariable("id") Long id, @Valid @RequestBody AlbumRequestDto request) {
        try {
            AlbumRequestDto albumDto = this.albumService.updateAlbum(id, request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(albumDto.getClass()).toUri();
            return ResponseEntity.created(uri).body("Album " + request.getNomeAlbum() + " atualizado com sucesso!");
        } catch (ResourceNotFoundException | CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/avaliar-album/{id}/")
    public ResponseEntity<String> avaliarAlbum(@PathVariable("id") Long id, @Valid @RequestBody AvaliacaoRequestDto request) {
        try {
            double result = this.avaliacaoService.avaliarAlbum(id, request);
            return ResponseEntity.ok("Nova média para album: " +result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar-albuns-simples/")
    public ResponseEntity<Object> listarAlbunsSimples() {
        try {
            List<AlbumResponseDto> albuns = this.albumService.getAllAlbumSimples();
            return ResponseEntity.ok().body(albuns);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar-albuns-completos/")
    public ResponseEntity<Object> listarAlbunsCompletos() {
        try {
            List<AlbumResponseFullDto> albuns = albumService.getAllAlbumCompletos();
            return ResponseEntity.ok().body(albuns);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{nome}/")
    public ResponseEntity<Page<AlbumResponseFullDto>> searchSetor(@PathVariable String nome, @PageableDefault(size = 20, page = 0) Pageable paginacao) {
        Page<AlbumResponseFullDto> pesquisaAlbum = albumService.searchAlbum(nome, paginacao);
        return ResponseEntity.ok().body(pesquisaAlbum);
    }

    @DeleteMapping("/delete/{id}/")
    public ResponseEntity<String> deleteAlbum(@PathVariable("id") Long id) {
        try {
            albumService.deleteAlbum(id);
            return ResponseEntity.ok().body("Album excluída com sucesso!");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}