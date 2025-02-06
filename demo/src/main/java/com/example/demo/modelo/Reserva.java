package com.example.demo.modelo;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    
    @ManyToOne
    private Horario horario;
    
    @ManyToOne
    private Instalacion instalacion;  // Relación con Instalacion

    @Column(nullable = false)
    private LocalDate fecha;

    // Métodos para obtener hora de inicio y hora de fin a partir del horario asociado
    public LocalTime getHoraInicio() {
        return this.horario.getHoraInicio();  // Devuelve la hora de inicio del horario
    }

    public LocalTime getHoraFin() {
        return this.horario.getHoraFin();  // Devuelve la hora de fin del horario
    }
}
