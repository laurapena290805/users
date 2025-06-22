package com.nova.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
}

