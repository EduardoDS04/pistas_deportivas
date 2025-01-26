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

import java.util.Optional;

@Controller
public class ControUsuario {

    @Autowired
    private RepoUsuario repoUsuario;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String formRegistro(Model modelo) {
        modelo.addAttribute("usuario", new Usuario());
        return "usuario/register";
    }

    @PostMapping("/register")
public String registrarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
    // Verificar si el username ya existe
    Optional<Usuario> usuarioExistente = repoUsuario.findByUsername(usuario.getUsername());
    if (usuarioExistente.isPresent()) {
        redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está en uso.");
        return "redirect:/register";
    }

    // Encriptar la contraseña
    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

    // Habilitar el usuario
    usuario.setEnabled(true);

    // Guardar en la base de datos
    repoUsuario.save(usuario);

    redirectAttributes.addFlashAttribute("success", "Usuario registrado con éxito. Ahora puede iniciar sesión.");
    return "redirect:/login";
}

}
