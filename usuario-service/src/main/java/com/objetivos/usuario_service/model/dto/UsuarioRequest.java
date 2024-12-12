package com.objetivos.usuario_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioRequest {
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
}
