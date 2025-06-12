// UpdateUserRequest.java
package com.nova.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
    private String nombre;
    private String apellido;
    private String direccion;
    private String ciudad;
    private String pais;
    private String telefono;
}