package com.gravadoracampista.repository;

import com.gravadoracampista.model.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String idFuncional);
    Optional<Usuario> findByUsernameAndExcluidoFalse(String idFuncional);
    Optional<Usuario> findByEmailAndExcluidoFalse(String email);

  Collection<Object> findByExcluidoFalse();
}