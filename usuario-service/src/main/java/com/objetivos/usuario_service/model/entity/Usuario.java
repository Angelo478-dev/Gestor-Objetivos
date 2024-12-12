package com.objetivos.usuario_service.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usuario_id")
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    @Override
    public String toString() {
        return String.format("%s %s - %s - %s", nombre, apellido, email, telefono);
    }
}
