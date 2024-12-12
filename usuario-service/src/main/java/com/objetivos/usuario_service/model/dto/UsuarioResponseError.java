package com.objetivos.usuario_service.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UsuarioResponseError {

    private String mensaje;

    public UsuarioResponseError(String mensaje) {
        this.mensaje = mensaje;
    }
}
