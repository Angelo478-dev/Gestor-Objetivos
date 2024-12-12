package com.objetivos.objetivo_service.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ObjetivoResponseError {
    private String mensaje;
    public ObjetivoResponseError(String mensaje) { this.mensaje = mensaje;}
}
