package com.objetivos.usuario_service.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class UsuarioResponseMessage {

    private String mensaje;

    public UsuarioResponseMessage(String mensaje) { this.mensaje = mensaje; }
}