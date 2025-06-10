package com.nova.users.controller;

import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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

    @GetMapping("/paginado")
    public ResponseEntity<Page<User>> listarUsuariosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "cedula") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> paginaUsuarios = service.listarUsuariosPaginados(pageable);
        return ResponseEntity.ok(paginaUsuarios);
    }

    @GetMapping("/paginado/rol")
    public ResponseEntity<Page<User>> listarUsuariosPorRol(
            @RequestParam Role rol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "cedula") String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> paginaUsuarios = service.listarPorRol(rol, pageable);
        return ResponseEntity.ok(paginaUsuarios);
    }
}

