package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.modelo.Usuario;
import com.example.demo.repositorio.RepoUsuario;

import java.security.Principal;
import java.util.Optional;

@Controller
public class ControDatos {

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder; // Se inyecta aquí el PasswordEncoder

    @GetMapping("/mis-datos")
    public String verMisDatos(Model modelo, Principal principal) {
        String username = principal.getName();
        Optional<Usuario> usuario = repoUsuario.findByUsername(username);

        if (usuario.isPresent()) {
            modelo.addAttribute("usuario", usuario.get());
            return "usuario/mis-datos";
        } else {
            modelo.addAttribute("titulo", "Error");
            modelo.addAttribute("mensaje", "Usuario no encontrado");
            return "error";
        }
    }

    @GetMapping("/usuario")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", repoUsuario.findAll());
        return "usuario/listar";
    }

    @GetMapping("/usuario/add")
    public String formNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/alta";
    }

    @PostMapping("/usuario/add")
public String crearUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
    try {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword())); // Encripta la contraseña
        repoUsuario.save(usuario); // Guarda el usuario en la base de datos
        redirectAttributes.addFlashAttribute("success", "Usuario creado con éxito.");
        return "redirect:/usuario";
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error al crear el usuario: " + e.getMessage());
        return "redirect:/usuario/add";
    }
}

}
