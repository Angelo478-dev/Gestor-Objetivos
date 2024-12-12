package com.objetivos.objetivo_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.objetivos.objetivo_service.model.dto.UsuarioDTO;
import com.objetivos.objetivo_service.repository.IUsuarioFeignClient;

@Service
public class UsuarioService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private IUsuarioFeignClient usuarioFeignClient;

    public List<UsuarioDTO> obtenerUsuarios() {
        return usuarioFeignClient.obtenerUsuarios();
    }

    public UsuarioDTO obtenerUsuarioPorId(Long id) {
        return usuarioFeignClient.obtenerUsuarioPorId(id);  // Llamada al servicio a través de Feign
    }
/* 
    public List<UsuarioDTO> obtenerUsuarios() {
        ResponseEntity<UsuarioDTO[]> response = restTemplate.getForEntity(
            "http://usuario-service/api/usuarios", 
            UsuarioDTO[].class);
        UsuarioDTO[] usuarioDTOs = response.getBody();
        List<UsuarioDTO> usuarios = Arrays.asList(usuarioDTOs);
        return usuarios;
    }*/
    
}
