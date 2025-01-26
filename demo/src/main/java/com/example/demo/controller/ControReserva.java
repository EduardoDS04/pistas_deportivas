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
import com.example.demo.modelo.Reserva;
import com.example.demo.modelo.Usuario;
import com.example.demo.repositorio.RepoHorario;
import com.example.demo.repositorio.RepoInstalacion;
import com.example.demo.repositorio.RepoReserva;
import com.example.demo.repositorio.RepoUsuario;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/reservas")
public class ControReserva {

    @Autowired
    private RepoReserva repoReserva;

    @Autowired
    private RepoHorario repoHorario;

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private RepoInstalacion repoInstalacion;

    @GetMapping
    public String listarReservas(@RequestParam(name = "usuarioId", required = false) Long usuarioId,
                                 @RequestParam(name = "instalacionId", required = false) Long instalacionId,
                                 @RequestParam(name = "page", defaultValue = "0") int page,
                                 Model modelo) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Reserva> reservas;

        if (usuarioId != null) {
            Optional<Usuario> usuario = repoUsuario.findById(usuarioId);
            if (usuario.isPresent()) {
                reservas = repoReserva.findByUsuario(usuario.get(), pageable);
                modelo.addAttribute("titulo", "Reservas del Usuario: " + usuario.get().getUsername());
            } else {
                modelo.addAttribute("titulo", "Error");
                modelo.addAttribute("mensaje", "Usuario no encontrado");
                return "error";
            }
        } else if (instalacionId != null) {
            reservas = repoReserva.findByInstalacion(instalacionId, pageable);
            modelo.addAttribute("titulo", "Reservas de la Instalación ID: " + instalacionId);
        } else {
            reservas = repoReserva.findAll(pageable);
            modelo.addAttribute("titulo", "Todas las Reservas");
        }

        modelo.addAttribute("reservas", reservas);
        modelo.addAttribute("usuarios", repoUsuario.findAll());
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        return "reserva/reservas";
    }


    @GetMapping("/add")
    public String formNuevaReserva(Model modelo) {
        modelo.addAttribute("usuarios", repoUsuario.findAll());
        modelo.addAttribute("horarios", repoHorario.findAll());
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        modelo.addAttribute("reserva", new Reserva());
        return "reserva/reservas-add";
    }

    @PostMapping("/add")
    public String guardarReserva(@ModelAttribute Reserva reserva, RedirectAttributes redirectAttributes) {
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas en fechas pasadas.");
            return "redirect:/admin/reservas/add";
        }

        if (reserva.getFecha().isAfter(LocalDate.now().plusWeeks(1))) {
            redirectAttributes.addFlashAttribute("error", "No se pueden realizar reservas con más de una semana de antelación.");
            return "redirect:/admin/reservas/add";
        }

        if (repoReserva.existsByUsuarioAndFecha(reserva.getUsuario(), reserva.getFecha())) {
            redirectAttributes.addFlashAttribute("error", "El usuario ya tiene una reserva en esta fecha.");
            return "redirect:/admin/reservas/add";
        }

        repoReserva.save(reserva);
        redirectAttributes.addFlashAttribute("success", "Reserva creada correctamente.");
        return "redirect:/admin/reservas";
    }

    @GetMapping("/edit/{id}")
    public String formEditarReserva(@PathVariable Long id, Model modelo) {
        Optional<Reserva> reserva = repoReserva.findById(id);
        if (reserva.isPresent()) {
            modelo.addAttribute("usuarios", repoUsuario.findAll());
            modelo.addAttribute("horarios", repoHorario.findAll());
            modelo.addAttribute("instalaciones", repoInstalacion.findAll());
            modelo.addAttribute("reserva", reserva.get());
            return "reserva/reservas-edit";
        } else {
            modelo.addAttribute("titulo", "Error");
            modelo.addAttribute("mensaje", "Reserva no encontrada");
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String actualizarReserva(@PathVariable Long id, @ModelAttribute Reserva reserva, RedirectAttributes redirectAttributes) {
        Optional<Reserva> reservaExistente = repoReserva.findById(id);
        if (reservaExistente.isPresent() && reservaExistente.get().getFecha().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "No se puede modificar una reserva pasada.");
            return "redirect:/admin/reservas";
        }

        repoReserva.save(reserva);
        redirectAttributes.addFlashAttribute("success", "Reserva actualizada correctamente.");
        return "redirect:/admin/reservas";
    }

    @GetMapping("/delete/{id}")
    public String formEliminarReserva(@PathVariable Long id, Model modelo) {
        Optional<Reserva> reserva = repoReserva.findById(id);
        if (reserva.isPresent()) {
            modelo.addAttribute("reserva", reserva.get());
            return "reserva/reservas-delete";
        } else {
            modelo.addAttribute("titulo", "Error");
            modelo.addAttribute("mensaje", "Reserva no encontrada");
            return "error";
        }
    }

    @PostMapping("/delete/{id}")
    public String eliminarReserva(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Reserva> reserva = repoReserva.findById(id);
        if (reserva.isPresent()) {
            if (reserva.get().getFecha().isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error", "No se puede eliminar una reserva pasada.");
                return "redirect:/admin/reservas";
            }
            repoReserva.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Reserva eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Reserva no encontrada.");
        }
        return "redirect:/admin/reservas";
    }

    @GetMapping("/horarios/{instalacionId}")
    @ResponseBody
    public List<Horario> obtenerHorariosPorInstalacion(@PathVariable Long instalacionId) {
        return repoHorario.findByInstalacionId(instalacionId);
    }
}

