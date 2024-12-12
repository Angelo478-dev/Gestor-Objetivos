package com.objetivos.objetivo_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjetivoRequest {

    private String titulo;
    private String descripcion;
    private String fechaLimite;
    private Integer completado;
    private Long usuarioId;
}
