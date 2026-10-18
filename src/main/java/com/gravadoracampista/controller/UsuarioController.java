package com.gravadoracampista.controller;

import com.gravadoracampista.dtos.request.LoginRequestDto;
import com.gravadoracampista.dtos.request.RegisterRequestDto;
import com.gravadoracampista.dtos.request.UpdateUserRequestDto;
import com.gravadoracampista.dtos.response.LoginResponseDto;
import com.gravadoracampista.dtos.response.UserResponseDto;
import com.gravadoracampista.service.AuthorizationService;
import com.gravadoracampista.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthorizationService authorizationService;

    @PostMapping(value = "/post/registro/")
    public Object registerUser(@Valid @RequestBody RegisterRequestDto request) {
        RegisterRequestDto userDto = this.usuarioService.registerOfUser(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDto.getClass()).toUri();
        return ResponseEntity.created(uri).body("Usuário " + request.getPrimeiroNome()+ " criado com sucesso!");
    }

    @PostMapping("/post/login/")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto request) throws Exception {
        try {
            LoginResponseDto result = this.authorizationService.login(request);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}/")
    public ResponseEntity<Object> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDto request) {
        try {
            usuarioService.updateUser(id, request);
            return ResponseEntity.ok("Usuário atualizado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}/")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        try {
            usuarioService.deleteUser(id);
            return ResponseEntity.ok("Usuário deletado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = usuarioService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}