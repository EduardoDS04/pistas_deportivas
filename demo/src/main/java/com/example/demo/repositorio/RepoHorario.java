package com.example.demo.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Horario;
import com.example.demo.modelo.Instalacion;


@Repository
public interface RepoHorario extends JpaRepository<Horario,Integer> {

    Page<Horario> findByInstalacion(Instalacion instalacion, Pageable pageable);
    Optional<Horario> findById(Long id);
    List<Horario> findByInstalacionId(Long instalacionId);
    List<Horario> findByInstalacion(Instalacion instalacion);
    boolean existsById(Long id);
    void deleteById(Long id);
    
}
