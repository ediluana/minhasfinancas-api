package com.ediluana.minhasfinancas.model.repository;

import com.ediluana.minhasfinancas.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

boolean existsByEmail(String email);

Optional<Usuario> findByEmail(String email); //evita null excepition

}
