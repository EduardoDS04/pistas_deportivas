package com.example.demo.modelo;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Instalacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @OneToMany(mappedBy = "instalacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Horario> horarios;

    // Constructor adicional para inicialización fácil (opcional)
    public Instalacion(String nombre) {
        this.nombre = nombre;
    }
}
