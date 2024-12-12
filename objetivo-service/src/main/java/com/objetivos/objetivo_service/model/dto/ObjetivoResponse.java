package com.objetivos.objetivo_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjetivoResponse {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaLimite;
    private String completado;
    private Long usuarioId;

    private String usuarioNombre;
}
