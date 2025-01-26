package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import com.example.demo.modelo.Instalacion;
import com.example.demo.repositorio.RepoInstalacion;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/instalaciones") // Ajuste para que coincida con el nombre de la carpeta
public class ControInstalacion {
    
    @Autowired 
    RepoInstalacion repoInstalacion;
    
    @GetMapping("/filtrar/{id}")
public String filtrarInstalaciones(@PathVariable Long id, Model model) {
    Optional<Instalacion> instalacionOptional = repoInstalacion.findById(id);
    if (instalacionOptional.isPresent()) {
        model.addAttribute("instalaciones", List.of(instalacionOptional.get()));
        model.addAttribute("instalacion", instalacionOptional.get());
    } else {
        model.addAttribute("instalaciones", repoInstalacion.findAll());
    }
    return "instalaciones/instalaciones";
}



    // Listar instalaciones
    @GetMapping("")
    public String getInstalaciones(Model model) {
        List<Instalacion> instalaciones = repoInstalacion.findAll();
        model.addAttribute("instalaciones", instalaciones);
        return "instalaciones/instalaciones"; // Ajuste a la vista correspondiente
    }

    // Agregar una instalación
    @GetMapping("/add")
    public String addInstalacion(Model modelo) {
        modelo.addAttribute("instalacion", new Instalacion());
        return "instalaciones/add"; // Ajuste a la vista correspondiente
    }

    @PostMapping("/add")
    public String addInstalacion(
        @ModelAttribute("instalacion") Instalacion instalacion)  {
        repoInstalacion.save(instalacion);
        return "redirect:/instalaciones";
    }

    // Editar una instalación
    @GetMapping("/edit/{id}")
    public String editInstalacion( 
        @PathVariable @NonNull Long id,
        Model modelo) {

        Optional<Instalacion> oInstalacion = repoInstalacion.findById(id);
        if (oInstalacion.isPresent()) {
            modelo.addAttribute("instalacion", oInstalacion.get());
            return "instalaciones/edit"; // Ajuste a la vista correspondiente
        } else {
            modelo.addAttribute("mensaje", "La instalación no existe");
            modelo.addAttribute("titulo", "Error editando instalación.");
            return "/error";
        }
    }

    @PostMapping("/edit/{id}")
    public String editInstalacion(
        @ModelAttribute("instalacion") Instalacion instalacion)  {
        repoInstalacion.save(instalacion);
        return "redirect:/instalaciones";
    }

    // Método GET para confirmar la eliminación
@GetMapping("/del/{id}")
public String confirmDelInstalacion( 
    @PathVariable @NonNull Long id,
    Model modelo) {

    Optional<Instalacion> oInstalacion = repoInstalacion.findById(id);
    if (oInstalacion.isPresent()) {
        modelo.addAttribute("instalacion", oInstalacion.get());
        return "instalaciones/delete"; // Ajuste a la vista correspondiente
    } else {
        modelo.addAttribute("mensaje", "La instalación no existe");
        modelo.addAttribute("titulo", "Error borrando instalación.");
        return "/error";
    }
}

    @PostMapping("/del/{id}")
public String delInstalacion(@PathVariable Long id, Model modelo) {
    if (repoInstalacion.existsById(id)) {
        repoInstalacion.deleteById(id); // La eliminación en cascada se encargará de los horarios
    } else {
        modelo.addAttribute("mensaje", "La instalación no existe");
        modelo.addAttribute("titulo", "Error borrando instalación.");
        return "/error";
    }
    return "redirect:/instalaciones";
}

    
}
