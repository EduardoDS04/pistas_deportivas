package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.modelo.Horario;
import com.example.demo.modelo.Instalacion;
import com.example.demo.modelo.Reserva;
import com.example.demo.modelo.Usuario;
import com.example.demo.repositorio.RepoHorario;
import com.example.demo.repositorio.RepoInstalacion;
import com.example.demo.repositorio.RepoReserva;
import com.example.demo.repositorio.RepoUsuario;

import lombok.NonNull;

@Controller
@RequestMapping("/admin/reservas")
public class ControReserva {

    @Autowired
    private RepoReserva repoReserva;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private RepoInstalacion repoInstalacion;
    
    @Autowired
    private RepoHorario repoHorario; // Para obtener la lista de horarios

    @GetMapping
    public String listarReservas(@RequestParam(name = "usuarioId", required = false) Long usuarioId,
                                 @RequestParam(name = "instalacionId", required = false) Long instalacionId,
                                 @RequestParam(name = "page", defaultValue = "0") int page, 
                                 Model modelo) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reserva> reservas;
    
        if (usuarioId != null) {
            Optional<Usuario> usuarioOpt = repoUsuario.findById(usuarioId);
            if (usuarioOpt.isPresent()) {
                reservas = repoReserva.findByUsuario(usuarioOpt.get(), pageable);
                modelo.addAttribute("titulo", "Reservas del Usuario: " + usuarioOpt.get().getUsername());
            } else {
                modelo.addAttribute("titulo", "Error");
                modelo.addAttribute("mensaje", "Usuario no encontrado");
                return "error";
            }
        } else if (instalacionId != null) {
            reservas = repoReserva.findByInstalacionId(instalacionId, pageable);
            modelo.addAttribute("titulo", "Reservas de la Instalación");
        } else {
            reservas = repoReserva.findAll(pageable);
            modelo.addAttribute("titulo", "Todas las Reservas");
        }
    
        modelo.addAttribute("reservas", reservas);
        modelo.addAttribute("usuarios", repoUsuario.findAll());
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        modelo.addAttribute("instalacionId", instalacionId); 
        modelo.addAttribute("usuarioId", usuarioId);  // Pasar el filtro de usuario
    
        return "reserva-admin/reservas"; 
    }
    

    // Mostrar formulario para crear una reserva (ADMIN)
    @GetMapping("/add")
    public String addReserva(Model modelo) {
        List<Usuario> usuarios = repoUsuario.findAll();
        List<Instalacion> instalaciones = repoInstalacion.findAll();
        List<Horario> horarios = repoHorario.findAll();

        modelo.addAttribute("usuarios", usuarios);
        modelo.addAttribute("instalaciones", instalaciones);
        modelo.addAttribute("horarios", horarios);
        modelo.addAttribute("reserva", new Reserva());
        return "reserva-admin/reservas-add"; // Vista para agregar reserva (admin)
    }

    // Guardar una nueva reserva (ADMIN)
    @PostMapping("/add")
    public String addReserva(@ModelAttribute("reserva") Reserva reserva,
                             RedirectAttributes redirectAttributes) {
        LocalDate hoy = LocalDate.now();

        // Validar si el usuario ya tiene una reserva en esa fecha
        if (repoReserva.existsByUsuarioAndFecha(reserva.getUsuario(), reserva.getFecha())) {
            redirectAttributes.addFlashAttribute("error", "El usuario " + reserva.getUsuario().getUsername() +
                    " ya tiene una reserva en la fecha " + reserva.getFecha());
            return "redirect:/admin/reservas/add";
        }
        // Validar que la fecha no sea pasada
        if (reserva.getFecha().isBefore(hoy)) {
            redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas para fechas pasadas.");
            return "redirect:/admin/reservas/add";
        }
        // Validar que la fecha no supere dos semanas de antelación
        if (reserva.getFecha().isAfter(hoy.plusWeeks(2))) {
            redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas con más de dos semanas de antelación.");
            return "redirect:/admin/reservas/add";
        }
        repoReserva.save(reserva);
        redirectAttributes.addFlashAttribute("success", "Reserva creada con éxito.");
        return "redirect:/admin/reservas";
    }

    @GetMapping("/edit/{id}")
    public String editReserva(@PathVariable Long id, Model modelo) {
        // Cargar la reserva sin validaciones adicionales
        Optional<Reserva> oReserva = repoReserva.findById(id);
        
        if (oReserva.isPresent()) {
            Reserva reserva = oReserva.get();
            
            // Cargar las listas de instalaciones, horarios y usuarios sin validaciones
            List<Instalacion> instalaciones = repoInstalacion.findAll();
            List<Horario> horarios = repoHorario.findAll();
            List<Usuario> usuarios = repoUsuario.findAll();
            
            modelo.addAttribute("instalaciones", instalaciones);
            modelo.addAttribute("horarios", horarios);
            modelo.addAttribute("usuarios", usuarios);
            modelo.addAttribute("reserva", reserva);
            
            return "reserva-admin/reservas-edit"; // Vista de edición
        } else {
            modelo.addAttribute("mensaje", "La reserva no existe");
            return "error"; // En caso de que la reserva no exista
        }
    }
    
    
    

    @PostMapping("/edit/{id}")
public String editReserva(@PathVariable Long id, @ModelAttribute("reserva") Reserva reserva, RedirectAttributes redirectAttributes) {
    LocalDate fechaActual = LocalDate.now();

    // Verificar existencia de la reserva
    Optional<Reserva> reservaOpt = repoReserva.findById(id);
    if (reservaOpt.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "La reserva no existe.");
        return "redirect:/admin/reservas";
    }

    // Validar que la fecha de la reserva sea posterior a hoy
    if (!reserva.getFecha().isAfter(fechaActual)) {
        redirectAttributes.addFlashAttribute("error", "La fecha debe ser posterior a hoy.");
        return "redirect:/admin/reservas/edit/" + id;
    }

    // Validar que la fecha no sea más de dos semanas en el futuro
    if (reserva.getFecha().isAfter(fechaActual.plusWeeks(2))) {
        redirectAttributes.addFlashAttribute("error", "La fecha no puede ser más de dos semanas en el futuro.");
        return "redirect:/admin/reservas/edit/" + id;
    }

    // Validar que el usuario no tenga otra reserva en la misma fecha
    if (repoReserva.existsByUsuarioAndFecha(reserva.getUsuario(), reserva.getFecha()) && !reservaOpt.get().getId().equals(reserva.getId())) {
        redirectAttributes.addFlashAttribute("error", "El usuario ya tiene una reserva en esa fecha.");
        return "redirect:/admin/reservas/edit/" + id;
    }

    // Actualizar la reserva
    Reserva reservaOriginal = reservaOpt.get();
    reservaOriginal.setFecha(reserva.getFecha());
    reservaOriginal.setInstalacion(reserva.getInstalacion());
    reservaOriginal.setHorario(reserva.getHorario());
    reservaOriginal.setUsuario(reserva.getUsuario());

    // Guardar los cambios
    repoReserva.save(reservaOriginal);
    redirectAttributes.addFlashAttribute("mensaje", "Reserva actualizada con éxito.");
    return "redirect:/admin/reservas";
}



    // Mostrar confirmación de eliminación de una reserva 
    @GetMapping("/delete/{id}")
    public String formEliminarReserva(@PathVariable Long id, Model modelo) {
        Optional<Reserva> reservaOptional = repoReserva.findById(id);
        if (!reservaOptional.isPresent()) {
            modelo.addAttribute("error", "Reserva no encontrada");
            return "error";
        }
        modelo.addAttribute("reserva", reservaOptional.get());
        return "reserva-admin/reservas-delete"; // Vista de confirmación de eliminación para admin
    }

    // Eliminar la reserva 
    @PostMapping("/delete/{id}")
    public String eliminarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Reserva> reservaOptional = repoReserva.findById(id);
        if (!reservaOptional.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Reserva no encontrada");
            return "redirect:/admin/reservas";
        }
        repoReserva.delete(reservaOptional.get());
        redirectAttributes.addFlashAttribute("success", "Reserva eliminada correctamente.");
        return "redirect:/admin/reservas";
    }
}
