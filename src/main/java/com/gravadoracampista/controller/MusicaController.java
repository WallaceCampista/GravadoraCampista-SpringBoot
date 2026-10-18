package com.gravadoracampista.controller;

import com.gravadoracampista.dtos.request.AvaliacaoRequestDto;
import com.gravadoracampista.dtos.request.MusicaRequestDto;
import com.gravadoracampista.dtos.response.MusicaResponseDto;
import com.gravadoracampista.dtos.response.MusicaResponseFullDto;
import com.gravadoracampista.exceptions.ResourceNotFoundException;
import com.gravadoracampista.service.AvaliacaoService;
import com.gravadoracampista.service.MusicaService;
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
@RequestMapping("/musica")
public class MusicaController {

    private final MusicaService musicaService;
    private final AvaliacaoService avaliacaoService;

    @PostMapping(value = "/novo-registro/")
    public ResponseEntity<String> registerMusica(@Valid @RequestBody MusicaRequestDto request) {
        try {
            MusicaRequestDto musicaDto = this.musicaService.registroDeMusica(request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(musicaDto.getClass()).toUri();
            return ResponseEntity.created(uri).body("Música " + request.getNomeMusica() + " criada com sucesso!");
        } catch (ResourceNotFoundException | CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}/")
    public ResponseEntity<String> updateMusica(@PathVariable("id") Long id, @Valid @RequestBody MusicaRequestDto request) {
        try {
            MusicaRequestDto musicaDto = this.musicaService.updateMusica(id, request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(musicaDto.getClass()).toUri();
            return ResponseEntity.created(uri).body("Música " + request.getNomeMusica() + " atualizada com sucesso!");
        } catch (ResourceNotFoundException | CreateEntitiesException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/avaliar-musica/{id}/")
    public ResponseEntity<String> avaliarMusica(@PathVariable("id") Long id, @Valid @RequestBody AvaliacaoRequestDto request) {
        try {
            double result = this.avaliacaoService.avaliarMusica(id, request);
            return ResponseEntity.ok("Nova média para musica: " +result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar-musicas-simples/")
    public ResponseEntity<Object> listarMusicasSimples() {
        try {
            List<MusicaResponseDto> musicas = this.musicaService.getAllMusicaSimples();
            return ResponseEntity.ok().body(musicas);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar-musicas-completas/")
    public ResponseEntity<Object> listarMusicasCompletas() {
        try {
            List<MusicaResponseFullDto> musicas = this.musicaService.getAllMusicaCompleta();
            return ResponseEntity.ok().body(musicas);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{nome}/")
    public ResponseEntity<Page<MusicaResponseFullDto>> searchMusica(@PathVariable String nome, @PageableDefault(size = 20, page = 0) Pageable paginacao) {
        Page<MusicaResponseFullDto> pesquisaMusica = musicaService.searchMusica(nome, paginacao);
        return ResponseEntity.ok().body(pesquisaMusica);
    }

    @DeleteMapping("/delete/{id}/")
    public ResponseEntity<String> deleteAlbum(@PathVariable("id") Long id) {
        try {
            musicaService.deleteMusica(id);
            return ResponseEntity.ok().body("Musica excluída com sucesso!");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}