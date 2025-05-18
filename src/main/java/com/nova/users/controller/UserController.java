package com.nova.users.controller;

import com.nova.users.model.User;
import com.nova.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public List<User> listarUsuarios() {
        return service.listarTodos();
    }

    @GetMapping("/{cedula}")
    public ResponseEntity<User> obtenerUsuario(@PathVariable String cedula) {
        return service.obtenerPorCedula(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public User crearUsuario(@RequestBody User usuario) {
        return service.crearUsuario(usuario);
    }

    @PutMapping("/{cedula}")
    public User actualizarUsuario(@PathVariable String cedula, @RequestBody User usuario) {
        return service.actualizarUsuario(cedula, usuario);
    }

    @DeleteMapping("/{cedula}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String cedula) {
        service.eliminarUsuario(cedula);
        return ResponseEntity.noContent().build();
    }
}

