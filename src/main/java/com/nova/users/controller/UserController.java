package com.nova.users.controller;

import com.nova.users.model.Role;
import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<User>> getAllUsers(Pageable pageable) {
        System.out.println("Intentando acceder a /users (listar todos)...");
        return ResponseEntity.ok(userRepository.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        System.out.println("Solicitud GET /users/" + id + " por usuario autenticado: " + currentUsername);

        User currentUser = userRepository.findByCorreo(currentUsername)
                .orElseThrow(() -> {
                    System.out.println("Usuario autenticado no encontrado en la base de datos");
                    return new RuntimeException("Usuario no encontrado");
                });

        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            System.out.println("Usuario con ID " + id + " no encontrado");
            return ResponseEntity.notFound().build();
        }

        if (!currentUser.getRol().equals(Role.ADMINISTRADOR) && !user.get().getCorreo().equals(currentUsername)) {
            System.out.println("Acceso denegado: el usuario " + currentUsername + " no tiene permisos para ver ID " + id);
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(user.get());
    }

    @PostMapping("/admin/repartidores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<User> createRepartidor(@RequestBody User user) {
        System.out.println("Creando repartidor con correo: " + user.getCorreo());
        if (userRepository.findByCorreo(user.getCorreo()).isPresent()) {
            System.out.println("Correo ya registrado: " + user.getCorreo());
            return ResponseEntity.badRequest().build();
        }
        user.setRol(Role.REPARTIDOR);
        user.setContrasena(userService.encodePassword(user.getContrasena()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @PostMapping("/admin/administradores")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<User> createAdministrador(@RequestBody User user) {
        System.out.println("Creando administrador con correo: " + user.getCorreo());
        if (userRepository.findByCorreo(user.getCorreo()).isPresent()) {
            System.out.println("Correo ya registrado: " + user.getCorreo());
            return ResponseEntity.badRequest().build();
        }
        user.setRol(Role.ADMINISTRADOR);
        user.setContrasena(userService.encodePassword(user.getContrasena()));
        return ResponseEntity.ok(userRepository.save(user));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Solicitud de perfil propio por: " + username);

        User user = userRepository.findByCorreo(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado en /me: " + username);
                    return new RuntimeException("Usuario no encontrado");
                });

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(@RequestBody User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Actualización de perfil por: " + username);

        User existingUser = userRepository.findByCorreo(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado en updateMyProfile");
                    return new RuntimeException("Usuario no encontrado");
                });

        existingUser.setNombre(updatedUser.getNombre());
        existingUser.setApellido(updatedUser.getApellido());
        existingUser.setDireccion(updatedUser.getDireccion());
        existingUser.setCiudad(updatedUser.getCiudad());
        existingUser.setPais(updatedUser.getPais());
        existingUser.setTelefono(updatedUser.getTelefono());

        return ResponseEntity.ok(userRepository.save(existingUser));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(@RequestBody String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        System.out.println("Actualización de contraseña por: " + username);

        User existingUser = userRepository.findByCorreo(username)
                .orElseThrow(() -> {
                    System.out.println("Usuario no encontrado en updateMyPassword");
                    return new RuntimeException("Usuario no encontrado");
                });

        existingUser.setContrasena(userService.encodePassword(newPassword));
        userRepository.save(existingUser);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        System.out.println("Intentando eliminar usuario " + id + " por " + currentUsername);

        User currentUser = userRepository.findByCorreo(currentUsername)
                .orElseThrow(() -> {
                    System.out.println("Usuario autenticado no encontrado en deleteUser");
                    return new RuntimeException("Usuario no encontrado");
                });

        Optional<User> userToDelete = userRepository.findById(id);
        if (userToDelete.isEmpty()) {
            System.out.println("Usuario con ID " + id + " no encontrado para eliminar");
            return ResponseEntity.notFound().build();
        }

        if (!currentUser.getRol().equals(Role.ADMINISTRADOR) && !userToDelete.get().getCorreo().equals(currentUsername)) {
            System.out.println("Acceso denegado al eliminar. Usuario actual: " + currentUsername + ", ID objetivo: " + id);
            return ResponseEntity.status(403).build();
        }

        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/rol/{rol}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Page<User>> getUsersByRol(@PathVariable Role rol, Pageable pageable) {
        System.out.println("Listando usuarios por rol: " + rol);
        return ResponseEntity.ok(userRepository.findByRol(rol, pageable));
    }

    @PutMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<User> updateUserByAdmin(@PathVariable String id, @RequestBody User updatedUser) {
        System.out.println("Admin actualizando usuario ID: " + id);

        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isEmpty()) {
            System.out.println("Usuario no encontrado para actualizar: " + id);
            return ResponseEntity.notFound().build();
        }

        User user = existingUser.get();
        user.setNombre(updatedUser.getNombre());
        user.setApellido(updatedUser.getApellido());
        user.setDireccion(updatedUser.getDireccion());
        user.setCiudad(updatedUser.getCiudad());
        user.setPais(updatedUser.getPais());
        user.setTelefono(updatedUser.getTelefono());
        user.setRol(updatedUser.getRol());

        return ResponseEntity.ok(userRepository.save(user));
    }
}
