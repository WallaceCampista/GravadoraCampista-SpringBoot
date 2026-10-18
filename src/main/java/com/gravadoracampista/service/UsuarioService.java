package com.gravadoracampista.service;

import com.gravadoracampista.dtos.request.RegisterRequestDto;
import com.gravadoracampista.dtos.request.UpdateUserRequestDto;
import com.gravadoracampista.dtos.response.UserResponseDto;
import com.gravadoracampista.model.entities.Usuario;
import com.gravadoracampista.repository.UsuarioRepository;
import com.gravadoracampista.service.exceptions.allExceptions.CreateEntitiesException;
import com.gravadoracampista.service.exceptions.userExceptions.UsernameNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository usuarioRepository;

  @Transactional(rollbackFor = Exception.class)
  public RegisterRequestDto registerOfUser(RegisterRequestDto createUsuarioRequestDto) {

    //CRIANDO LISTA DE POSSIVEIS ERROS
    List<String> errorMessages = new ArrayList<>();

    //VALIDACOES DE REGRAS DE NEGOCIO
    Optional<Usuario> existingUser = this.usuarioRepository.findByUsernameAndExcluidoFalse(createUsuarioRequestDto.getUsername());
    if (existingUser.isPresent()) {
      errorMessages.add("Esse usuario já existe no sistema.");
    }

    Optional<Usuario> existingEmail = this.usuarioRepository.findByEmailAndExcluidoFalse(createUsuarioRequestDto.getEmail());
    if (existingEmail.isPresent()) {
      errorMessages.add("Esse e-mail já existe no sistema.");
    }

    if (!(createUsuarioRequestDto.getEmail()).matches(".+@.+com")) {
      errorMessages.add("Informe um email válido.");
    }

    if (!createUsuarioRequestDto.getPassword().matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.]).{8,}")) {
      errorMessages.add("A senha a ser digitada deve conter no mínimo 8 caracteres, sendo obrigado possuir ao menos 1 caractere maiúsculo, 1 caractere minúsculo, 1 caractere especial e 1 número");
    }


    //VERIFICANDO SE A LISTA DE ERRO ESTA VAZIA. CASO CONTRARIO, RETORNA UMA EXCEÇÃO
    if (!errorMessages.isEmpty()) {
      throw new CreateEntitiesException(errorMessages);
    }

    //PERSISTINDO NO BANCO DE DADOS
    Usuario userEntity = new Usuario();

    createUsuarioRequestDto.setPassword(new BCryptPasswordEncoder().encode(createUsuarioRequestDto.getPassword()));

    userEntity.setUsername(createUsuarioRequestDto.getUsername());
    userEntity.setPassword(createUsuarioRequestDto.getPassword());
    userEntity.setEmail(createUsuarioRequestDto.getEmail());
    userEntity.setPrimeiroNome(createUsuarioRequestDto.getPrimeiroNome());
    userEntity.setSobrenome(createUsuarioRequestDto.getSobrenome());
    userEntity.setExcluido(false);
    userEntity.setCriadoEm(LocalDateTime.now());
    userEntity.setIsAdmin(false);

    this.usuarioRepository.saveAndFlush(userEntity);

    return createUsuarioRequestDto;
  }

  @Transactional(rollbackFor = Exception.class)
  public UserDetails findUsuarioByidFuncional(String username) {
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario não encontrado com idFuncional: " + username));
    return usuario;
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateUser(Long id, UpdateUserRequestDto request) {
    String msg = "Usuário não encontrado com id: " + id;
    Usuario user = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(msg));

    if(user.getExcluido()){
      throw new IllegalArgumentException(msg);
    }

    if(request.getUsername() != null){
      user.setUsername(request.getUsername());
    }
    if(request.getPassword() != null){
      user.setPassword(request.getPassword());
    }
    if (request.getEmail() != null){
      user.setEmail(request.getEmail());
    }
    if (request.getPrimeiroNome() != null){
      user.setPrimeiroNome(request.getPrimeiroNome());
    }
    if (request.getSobrenome() != null){
      user.setSobrenome(request.getSobrenome());
    }
    if (request.getIsAdmin() != null){
      user.setIsAdmin(request.getIsAdmin());
    }

    usuarioRepository.save(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteUser(Long id) {
    String msg = "Usuário não encontrado com id: " + id;
    Usuario user = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(msg));

    if(user.getExcluido()){
      throw new IllegalArgumentException(msg);
    }

    user.setExcluido(true);
    usuarioRepository.save(user);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<UserResponseDto> getAllUsers() {
    List<UserResponseDto> collect = usuarioRepository.findByExcluidoFalse().stream()
            .map((Object usuario) -> convertToDto((Usuario) usuario))
            .collect(Collectors.toList());
    return collect;
  }

  private UserResponseDto convertToDto(Usuario usuario) {
    return new UserResponseDto(
            usuario.getUsuarioId(),
            usuario.getUsername(),
            usuario.getPassword(),
            usuario.getEmail(),
            usuario.getPrimeiroNome(),
            usuario.getSobrenome(),
            usuario.getIsAdmin(),
            usuario.getCriadoEm(),
            usuario.getUltimoLogin()
    );
  }
}