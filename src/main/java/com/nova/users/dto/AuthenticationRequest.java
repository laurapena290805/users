package com.nova.users.dto;

import lombok.Data;

@Data
public class AuthenticationRequest {
    private String correo;
    private String contrasena;
}

