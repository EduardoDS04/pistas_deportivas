package com.example.demo.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.modelo.Horario;
import com.example.demo.modelo.Reserva;
import com.example.demo.modelo.Usuario;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepoReserva extends JpaRepository<Reserva, Long> {

    // Validar si un usuario ya tiene una reserva en la misma fecha
    boolean existsByUsuarioAndFecha(Usuario usuario, LocalDate fecha);

    // Consulta para encontrar reservas asociadas a una instalación
    Page<Reserva> findByInstalacionId(Long instalacionId, Pageable pageable);
    Page<Reserva> findByUsuario(Usuario usuario, Pageable pageable);

    // Consulta para encontrar reservas dentro de un rango de fechas
    List<Reserva> findByFechaBetween(LocalDate startDate, LocalDate endDate);

     // Método para verificar si existe una reserva para un horario específico
     boolean existsByHorario(Horario horario);
    boolean existsByUsuarioAndFechaAndIdNot(Usuario usuario, LocalDate fecha, Long id);
}
