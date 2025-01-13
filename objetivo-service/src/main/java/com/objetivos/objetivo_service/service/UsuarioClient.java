package com.objetivos.objetivo_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.objetivos.objetivo_service.model.dto.UsuarioDTO;

@Service
public class UsuarioClient {

    private final RestTemplate restTemplate;

    public UsuarioClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        String url = "http://localhost:8080/api/usuarios/" + id;
        return restTemplate.getForObject(url, UsuarioDTO.class);
    }
}