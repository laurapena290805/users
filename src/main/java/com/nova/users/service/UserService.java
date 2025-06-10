package com.nova.users.service;


import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.nova.users.model.Role;

import java.util.List;
import java.util.Optional;
import com.nova.users.model.User;


@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> listarTodos() {
        return repository.findAll();
    }

    public Optional<User> obtenerPorCedula(String cedula) {
        return repository.findById(cedula);
    }

    public User crearUsuario(User usuario) {
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        return repository.save(usuario);
    }
    public User actualizarUsuario(String cedula, User usuario) {
        if (usuario.getRol() == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        usuario.setCedula(cedula);
        return repository.save(usuario);
    }
    public void eliminarUsuario(String cedula) {
        repository.deleteById(cedula);
    }

    public Page<User> listarUsuariosPaginados(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<User> listarPorRol(Role rol, Pageable pageable) {
        return repository.findByRol(rol, pageable);
    }
}
