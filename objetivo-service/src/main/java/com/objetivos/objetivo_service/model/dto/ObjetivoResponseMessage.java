package com.objetivos.objetivo_service.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class ObjetivoResponseMessage {
    private String mensaje;
    public ObjetivoResponseMessage(String mensaje) { this.mensaje = mensaje;}

}
