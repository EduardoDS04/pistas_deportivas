package com.example.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.modelo.Rol;
import com.example.demo.modelo.Usuario;

import java.util.List;
import java.util.Optional;

public interface RepoUsuario extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    List<Usuario> findByTipo(Rol tipo); // Filtramos usuarios por rol
}
