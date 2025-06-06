package com.nova.users.service;


import com.nova.users.model.User;
import com.nova.users.repository.UserRepository;
import com.nova.users.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
}
