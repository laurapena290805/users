package com.nova.users.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private String cedula;
    private String nombre;
    private String apellido;
    private String correo;
    private String direccion;
    private String ciudad;
    private String codigoPostal;
    private String pais;
    private String telefono;
    private String rol;
    private String contraseña;
}
