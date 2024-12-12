package com.objetivos.usuario_service.service;

import com.objetivos.usuario_service.model.dto.UsuarioRequest;
import com.objetivos.usuario_service.model.dto.UsuarioResponse;
import com.objetivos.usuario_service.model.entity.Usuario;
import com.objetivos.usuario_service.repository.IUsuarioRepository;
import com.objetivos.usuario_service.service.util.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;

    // Constructor para inyección de dependencias
    public UsuarioService(IUsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Obtiene todos los usuarios.
     *
     * @return lista de usuarios.
     */
    public List<UsuarioResponse> getAllUsuarios() {
        var usuarios = usuarioRepository.findAll();
        return usuarios.stream().map(this::mapToUsuarioResponse).toList();
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id identificador del usuario.
     * @return usuario encontrado.
     * @throws ErrorMessage si no se encuentra el usuario.
     */
    public UsuarioResponse findById(Long id) {
        var usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            log.error("Usuario no encontrado con el ID {}", id);
            throw new ErrorMessage("Usuario no encontrado con el ID: " + id);
        }
        return mapToUsuarioResponse(usuario);
    }

    /**
     * Busca usuarios por su nombre.
     *
     * @param nombre nombre del usuario.
     * @return lista de usuarios encontrados.
     * @throws ErrorMessage si no se encuentran usuarios.
     */
    public List<UsuarioResponse> findByName(String nombre) {
        List<Usuario> usuarios = usuarioRepository.findByName(nombre);
        if (usuarios.isEmpty()) {
            log.error("No se encuentran usuarios con nombre {}", nombre);
            throw new ErrorMessage("No se encuentran usuarios con nombre: " + nombre);
        }

        return usuarios.stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }

    /**
     * Crea un nuevo usuario con datos de un DTO.
     *
     * @param usuarioRequest datos del usuario a crear.
     * @return el usuario creado.
     */
    public Usuario createUsuario(UsuarioRequest usuarioRequest) {
        try {

            validateUsuario(usuarioRequest);

            Usuario usuario = Usuario.builder()
                    .nombre(usuarioRequest.getNombre())
                    .apellido(usuarioRequest.getApellido())
                    .email(usuarioRequest.getEmail())
                    .telefono(usuarioRequest.getTelefono())
                    .build();

            usuarioRepository.save(usuario);
            log.info("Usuario agregado con éxito: {}", usuario);
            return usuario;

        }catch (IllegalArgumentException e) {
            log.error("Error de validación al crear usuario: {}", e.getMessage(), e);
            throw e; // Re-lanza la excepción si es necesario
        } catch (Exception e) {
            log.error("Error inexperado al crear usuario: ", e.getMessage());
            throw new RuntimeException("Error al crear el usuario", e);
        }
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param usuario entidad del usuario con los datos actualizados.
     * @return usuario actualizado.
     */
    public Usuario updateUsuario(Long id, UsuarioRequest usuarioRequest) {

        Usuario objectUsuario = usuarioRepository.findById(id).orElse(null);

        if (objectUsuario == null) {
            log.error("No se puede actualizar, no se encuentra usuario con ID: {}", id);
            throw new ErrorMessage("No se puede actualizar un usuario inexistente.");
        }

        objectUsuario.setTelefono(usuarioRequest.getTelefono());
        objectUsuario.setEmail(usuarioRequest.getEmail());
        log.info("Usuario con ID {} actualizado exitosamente", id);
        return usuarioRepository.save(objectUsuario);

    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar.
     * @throws ErrorMessage si el usuario no existe.
     */
    public String deleteUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            log.error("No se puede eliminar, el usuario no existe con el ID:{}", id);
            throw new ErrorMessage("No se puede eliminar, el usuario no existe con el ID: " + id);
        }
        usuarioRepository.deleteById(id);
        log.info("Usuario con ID {} eliminado exitosamente", id);

        return String.format("Usuario %s eliminado exitosamente", id);
    }

    private UsuarioResponse mapToUsuarioResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId() != null ? usuario.getId() : null)
                .nombre(usuario.getNombre())
                .apellido(usuario.getApellido())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .build();
    }

    private void validateUsuario(UsuarioRequest usuarioRequest) {
        if (usuarioRequest.getNombre() == null || usuarioRequest.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio.");
        }
        if (usuarioRequest.getApellido() == null || usuarioRequest.getApellido().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacio.");
        }
    }
}
