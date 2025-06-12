package com.nova.users.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
}

