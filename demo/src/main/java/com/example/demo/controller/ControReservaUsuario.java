package com.example.demo.controller;

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

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mis-datos")
public class ControReservaUsuario {

    @Autowired
    private RepoReserva repoReserva;

    @Autowired
    private RepoHorario repoHorario;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private RepoInstalacion repoInstalacion;

    // Mostrar las reservas del usuario
    @GetMapping("/mis-reservas")
    public String misReservas(Principal principal, @RequestParam(defaultValue = "0") int page, Model modelo) {
        Optional<Usuario> usuarioOptional = repoUsuario.findByUsername(principal.getName());
        if (usuarioOptional.isEmpty()) {
            modelo.addAttribute("error", "Usuario no encontrado");
            return "error";
        }
        Usuario usuario = usuarioOptional.get();
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reserva> reservas = repoReserva.findByUsuario(usuario, pageable);
        modelo.addAttribute("reservas", reservas);
        return "reserva/mis-reservas";
    }

    // Proporciona los horarios según la instalación seleccionada
    @GetMapping("/horarios/{instalacionId}")
    @ResponseBody
    public List<Horario> obtenerHorariosPorInstalacion(@PathVariable Long instalacionId) {
        return repoHorario.findByInstalacionId(instalacionId);
    }

    // Mostrar formulario para añadir una reserva
    @GetMapping("/add")
    public String addReserva(Model modelo) {
        List<Instalacion> instalaciones = repoInstalacion.findAll();
        List<Horario> horarios = repoHorario.findAll();
        List<Usuario> usuarios = repoUsuario.findAll();

        modelo.addAttribute("instalaciones", instalaciones);
        modelo.addAttribute("horarios", horarios);
        modelo.addAttribute("usuarios", usuarios);
        modelo.addAttribute("reserva", new Reserva());

        return "reserva/reservas-add";
    }

   // Guardar una nueva reserva (método POST)
@PostMapping("/add")
public String addReserva(@ModelAttribute("reserva") Reserva reserva,
                         Principal principal,
                         RedirectAttributes redirectAttributes) {
    LocalDate hoy = LocalDate.now();

    // Obtener el usuario autenticado
    Optional<Usuario> usuarioOptional = repoUsuario.findByUsername(principal.getName());
    if (usuarioOptional.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Usuario no encontrado");
        return "redirect:/mis-datos/add";
    }
    Usuario usuario = usuarioOptional.get();
    // Asignar el usuario autenticado a la reserva
    reserva.setUsuario(usuario);

    // Validar si el usuario ya tiene una reserva en esa fecha
    if (repoReserva.existsByUsuarioAndFecha(usuario, reserva.getFecha())) {
        redirectAttributes.addFlashAttribute("error", "El usuario " + usuario.getUsername() +
                " ya tiene una reserva en la fecha " + reserva.getFecha());
        return "redirect:/mis-datos/add";
    }

    // Validar si la fecha es anterior a la actual
    if (reserva.getFecha().isBefore(hoy)) {
        redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas para fechas pasadas.");
        return "redirect:/mis-datos/add";
    }

    // Validar si la fecha es mayor a dos semanas desde hoy
    if (reserva.getFecha().isAfter(hoy.plusWeeks(2))) {
        redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas con más de dos semanas de antelación.");
        return "redirect:/mis-datos/add";
    }

    // Guardar la reserva (ahora asociada al usuario)
    repoReserva.save(reserva);
    redirectAttributes.addFlashAttribute("mensaje", "Reserva creada con éxito.");
    return "redirect:/mis-datos/mis-reservas";
}

    // Mostrar el formulario de edición de una reserva
    @GetMapping("/editar/{id}")
    public String formEditarReserva(@PathVariable Long id, Principal principal, Model modelo) {
        Optional<Reserva> reservaOptional = repoReserva.findById(id);
        if (reservaOptional.isEmpty()) {
            modelo.addAttribute("error", "Reserva no encontrada");
            return "error";
        }
        Reserva reserva = reservaOptional.get();
        Usuario usuario = repoUsuario.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!reserva.getUsuario().equals(usuario)) {
            modelo.addAttribute("error", "No tienes permiso para editar esta reserva");
            return "error";
        }
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            modelo.addAttribute("error", "No se puede editar una reserva pasada");
            return "error";
        }
        modelo.addAttribute("reserva", reserva);
        modelo.addAttribute("horarios", repoHorario.findAll());
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        return "reserva/reservas-edit";
    }

    @PostMapping("/editar/{id}")
public String actualizarReserva(@PathVariable Long id, @ModelAttribute Reserva reserva, Principal principal, RedirectAttributes redirectAttributes) {
    Optional<Reserva> reservaExistente = repoReserva.findById(id);
    if (reservaExistente.isEmpty()) {
        redirectAttributes.addFlashAttribute("error", "Reserva no encontrada");
        return "redirect:/mis-datos/mis-reservas";
    }
    Reserva reservaOriginal = reservaExistente.get();
    Usuario usuario = repoUsuario.findByUsername(principal.getName())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    if (!reservaOriginal.getUsuario().equals(usuario)) {
        redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta reserva");
        return "redirect:/mis-datos/mis-reservas";
    }
    if (reserva.getFecha().isBefore(LocalDate.now())) {
        redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas en fechas pasadas.");
        return "redirect:/mis-datos/mis-reservas";
    }
    if (reserva.getFecha().isAfter(LocalDate.now().plusWeeks(2))) {
        redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas con más de dos semanas de antelación.");
        return "redirect:/mis-datos/mis-reservas";
    }
    
    // Solo se realiza la validación de duplicados si la fecha se modificó
    if (!reserva.getFecha().equals(reservaOriginal.getFecha())) {
        if (repoReserva.existsByUsuarioAndFechaAndIdNot(usuario, reserva.getFecha(), id)) {
            redirectAttributes.addFlashAttribute("error", "Ya tienes una reserva para esta fecha.");
            return "redirect:/mis-datos/mis-reservas";
        }
    }
    
    reservaOriginal.setFecha(reserva.getFecha());
    reservaOriginal.setInstalacion(reserva.getInstalacion());
    reservaOriginal.setHorario(reserva.getHorario());
    repoReserva.save(reservaOriginal);
    redirectAttributes.addFlashAttribute("success", "Reserva actualizada correctamente.");
    return "redirect:/mis-datos/mis-reservas";
}




    // Mostrar confirmación de eliminación de una reserva
    @GetMapping("/eliminar/{id}")
    public String formEliminarReserva(@PathVariable Long id, Principal principal, Model modelo) {
        Optional<Reserva> reservaOptional = repoReserva.findById(id);
        if (reservaOptional.isEmpty()) {
            modelo.addAttribute("error", "Reserva no encontrada");
            return "error";
        }
        Reserva reserva = reservaOptional.get();
        Usuario usuario = repoUsuario.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!reserva.getUsuario().equals(usuario)) {
            modelo.addAttribute("error", "No tienes permiso para eliminar esta reserva");
            return "error";
        }
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            modelo.addAttribute("error", "No se puede eliminar una reserva pasada");
            return "error";
        }
        modelo.addAttribute("reserva", reserva);
        return "reserva/reservas-delete";
    }

    // Eliminar la reserva
    @PostMapping("/eliminar/{id}")
    public String eliminarReserva(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Optional<Reserva> reservaOptional = repoReserva.findById(id);
        if (reservaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Reserva no encontrada");
            return "redirect:/mis-datos/mis-reservas";
        }
        Reserva reserva = reservaOptional.get();
        Usuario usuario = repoUsuario.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!reserva.getUsuario().equals(usuario)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta reserva");
            return "redirect:/mis-datos/mis-reservas";
        }
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar una reserva pasada");
            return "redirect:/mis-datos/mis-reservas";
        }
        repoReserva.delete(reserva);
        redirectAttributes.addFlashAttribute("success", "Reserva eliminada correctamente.");
        return "redirect:/mis-datos/mis-reservas";
    }
}
