package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private PasswordEncoder passwordEncoder;

    @GetMapping("/mis-datos")
    public String verMisDatos(Model modelo, Principal principal) {
        String username = principal.getName();
        Optional<Usuario> usuarioOpt = repoUsuario.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            modelo.addAttribute("usuario", usuarioOpt.get());
            return "usuario/mis-datos";
        } else {
            modelo.addAttribute("titulo", "Error");
            modelo.addAttribute("mensaje", "Usuario no encontrado");
            return "error";
        }
    }

    // Listar todos los usuarios
    @GetMapping("/usuario")
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", repoUsuario.findAll());
        return "usuario/listar";  // Vista donde muestra el maestro-detalle
    }

    // Formulario para dar de alta un usuario
    @GetMapping("/usuario/add")
    public String formNuevoUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/alta";
    }

    // Procesamos el alta de usuario
    @PostMapping("/usuario/add")
    public String crearUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            repoUsuario.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Usuario creado con éxito.");
            return "redirect:/usuario";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario: " + e.getMessage());
            return "redirect:/usuario/add";
        }
    }

    // Formulario que edita un usuario existente
    @GetMapping("/usuario/edit/{id}")
    public String formEditarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = repoUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuario";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        return "usuario/editar"; 
    }

    // Procesar la edición del usuario
    @PostMapping("/usuario/edit/{id}")
    public String editarUsuario(@PathVariable Long id,
                                @ModelAttribute Usuario usuario,
                                RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = repoUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuario";
        }
        Usuario usuarioExistente = usuarioOpt.get();

        try {
            usuarioExistente.setUsername(usuario.getUsername());
            usuarioExistente.setEmail(usuario.getEmail());
            // Si quieres cambiar la contraseña solo si no viene en blanco:
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
            }
            usuarioExistente.setTipo(usuario.getTipo());
            repoUsuario.save(usuarioExistente);
            redirectAttributes.addFlashAttribute("success", "Usuario editado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al editar el usuario: " + e.getMessage());
        }
        return "redirect:/usuario";
    }

    //  eliminación de usuario
    @GetMapping("/usuario/delete/{id}")
    public String formEliminarUsuario(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = repoUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuario";
        }
        model.addAttribute("usuario", usuarioOpt.get());
        return "usuario/eliminar";  // Vista de confirmación
    }

    // Procesamos la eliminación de usuario
    @PostMapping("/usuario/delete/{id}")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = repoUsuario.findById(id);
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuario";
        }
        try {
            repoUsuario.delete(usuarioOpt.get());
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/usuario";
    }
}
