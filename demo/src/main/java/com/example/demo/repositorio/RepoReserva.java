package com.example.demo.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Reserva;
import com.example.demo.modelo.Usuario;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepoReserva extends JpaRepository<Reserva, Long> {

    // Validar si un usuario ya tiene una reserva en la misma fecha
    @Query("SELECT COUNT(r) > 0 FROM Reserva r WHERE r.usuario = :usuario AND r.fecha = :fecha")
    boolean existsByUsuarioAndFecha(@Param("usuario") Usuario usuario, @Param("fecha") LocalDate fecha);

    // Buscar reservas por instalación
    @Query("SELECT r FROM Reserva r WHERE r.horario.instalacion.id = :instalacionId")
    List<Reserva> findByInstalacion(@Param("instalacionId") Long instalacionId);
    

    // Buscar reservas por usuario
    List<Reserva> findByUsuario(Usuario usuario);

    // Buscar reservas por rango de fechas
    @Query("SELECT r FROM Reserva r WHERE r.fecha BETWEEN :startDate AND :endDate")
    List<Reserva> findByFechaBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    Page<Reserva> findByUsuario(Usuario usuario, Pageable pageable);

    // Consulta para encontrar reservas asociadas a una instalación
    @Query("SELECT r FROM Reserva r WHERE r.horario.instalacion.id = :instalacionId")
    Page<Reserva> findByInstalacion(@Param("instalacionId") Long instalacionId, Pageable pageable);
}
