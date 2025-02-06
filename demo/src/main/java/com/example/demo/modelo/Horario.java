package com.example.demo.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "instalacion_id", nullable = false)
    private Instalacion instalacion;

    private LocalTime horaInicio;

    private LocalTime horaFin;
    public Horario(Instalacion instalacion, LocalTime horaInicio, LocalTime horaFin) {
        this.instalacion = instalacion;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }
}
