package com.objetivos.objetivo_service.model.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ObjetivoDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaLimite;
    private String completado;
    private Long usuarioId;
    private String usuarioNombre;
}
