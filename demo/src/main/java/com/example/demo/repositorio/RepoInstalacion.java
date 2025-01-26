package com.example.demo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.modelo.Instalacion;


@Repository
public interface RepoInstalacion extends JpaRepository<Instalacion,Long> {
    
}
