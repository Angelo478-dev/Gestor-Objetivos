package com.objetivos.objetivo_service.repository;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.objetivos.objetivo_service.model.dto.UsuarioDTO;

@FeignClient(name = "usuario-service")
public interface IUsuarioFeignClient {
    @GetMapping("/api/usuarios")
    List<UsuarioDTO> obtenerUsuarios();

    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable("id") Long id);
}