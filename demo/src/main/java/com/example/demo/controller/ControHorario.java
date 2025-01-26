package com.example.demo.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.modelo.Horario;
import com.example.demo.modelo.Instalacion;
import com.example.demo.repositorio.RepoHorario;
import com.example.demo.repositorio.RepoInstalacion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/horario")
public class ControHorario {
    
    @Autowired 
    RepoHorario repoHorario;

    @Autowired
    RepoInstalacion repoInstalacion;

    @GetMapping("/filtrar/{id}")
public String filtrarHorarios(@PathVariable Long id, Model model, Pageable pageable) {
    Optional<Instalacion> instalacionOptional = repoInstalacion.findById(id);
    if (instalacionOptional.isPresent()) {
        Page<Horario> horarios = repoHorario.findByInstalacion(instalacionOptional.get(), pageable);
        model.addAttribute("horarios", horarios.getContent());
        model.addAttribute("page", horarios);
        model.addAttribute("instalaciones", repoInstalacion.findAll());
        model.addAttribute("instalacion", instalacionOptional.get());
    } else {
        model.addAttribute("horarios", repoHorario.findAll(pageable).getContent());
        model.addAttribute("instalaciones", repoInstalacion.findAll());
    }
    return "horarios/horarios";
}


    // Lista todos los horarios
    @GetMapping("")
    public String getHorarios(
        Model model,
        @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<Horario> page = repoHorario.findAll(pageable);
        model.addAttribute("page", page);
        model.addAttribute("horarios", page.getContent());
        model.addAttribute("instalaciones", repoInstalacion.findAll());
        return "horarios/horarios";
    }

    // Formulario para añadir un horario
    @GetMapping("/add")
    public String addHorario(Model modelo) {
        modelo.addAttribute("horario", new Horario());
        modelo.addAttribute("operacion", "ADD");
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        return "/horarios/add";
    }

    @PostMapping("/add")
    public String addHorario(@ModelAttribute("horario") Horario horario) {
        repoHorario.save(horario);
        return "redirect:/horario";
    }

    @GetMapping("/edit/{id}")
public String editHorario(@PathVariable @NonNull Long id, Model modelo) {
    Optional<Horario> oHorario = repoHorario.findById(id);
    if (oHorario.isPresent()) {
        modelo.addAttribute("horario", oHorario.get());
        modelo.addAttribute("operacion", "EDIT");
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        return "/horarios/edit"; 
    } else {
        modelo.addAttribute("mensaje", "El horario no existe");
        modelo.addAttribute("titulo", "Error editando horario.");
        return "/error";
    }
}


    @PostMapping("/edit/{id}")
    public String editHorario(@PathVariable Long id, @ModelAttribute("horario") Horario horario) {
        if (repoHorario.existsById(id)) {
            repoHorario.save(horario);
            return "redirect:/horario";
        } else {
            return "redirect:/error";
        }
    }

   // Formulario para mostrar confirmación de borrar un horario
@GetMapping("/del/{id}")
public String showDeleteForm(@PathVariable @NonNull Long id, Model modelo) {
    Optional<Horario> oHorario = repoHorario.findById(id);
    if (oHorario.isPresent()) {
        modelo.addAttribute("horario", oHorario.get());
        modelo.addAttribute("operacion", "DEL");
        modelo.addAttribute("instalaciones", repoInstalacion.findAll());
        return "/horarios/delete";
    } else {
        modelo.addAttribute("mensaje", "El horario no existe");
        modelo.addAttribute("titulo", "Error borrando horario.");
        return "/error";
    }
}

@PostMapping("/del/{id}")
public String delHorario(@PathVariable Long id, Model model) {
    if (repoHorario.existsById(id)) {
        repoHorario.deleteById(id);
        return "redirect:/horario"; // Redirige a la lista de horarios tras el borrado
    } else {
        model.addAttribute("mensaje", "El horario no existe o no se puede eliminar.");
        return "/error";
    }
}


}
