package com.objetivos.usuario_service.controller;

import com.objetivos.usuario_service.model.dto.UsuarioRequest;
import com.objetivos.usuario_service.model.dto.UsuarioResponse;
import com.objetivos.usuario_service.model.dto.UsuarioResponseError;
import com.objetivos.usuario_service.model.dto.UsuarioResponseMessage;
import com.objetivos.usuario_service.model.entity.Usuario;
import com.objetivos.usuario_service.service.UsuarioService;
import com.objetivos.usuario_service.service.util.ErrorMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private final UsuarioService usuarioService;

    // Inyección de dependencias a través del constructor
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @return lista de usuarios.
     */
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Crea un nuevo usuario.
     *
     * @param usuarioRequest datos del usuario a crear.
     * @return el usuario creado.
     */
    @PostMapping
    public ResponseEntity<Object> crearUsuario(@Validated @RequestBody UsuarioRequest usuarioRequest) {

        try {
            Usuario usuario = usuarioService.createUsuario(usuarioRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (Exception e) {
            String mensajeError = "Ha ocurrido un error inesperado al crear el usuario: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UsuarioResponseError(mensajeError));
        }
    }

    /**
     * Obtiene un usuario por su ID.
     *
     * @param id identificador del usuario.
     * @return el usuario encontrado o un mensaje de error.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerUsuario(@PathVariable Long id) {
        try {
            UsuarioResponse usuario = usuarioService.findById(id);
            return ResponseEntity.ok(usuario);
        } catch (ErrorMessage ex) {
            // Devolver mensaje de error junto con el código HTTP 404
            UsuarioResponseError error = new UsuarioResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Busca usuarios por su nombre.
     *
     * @param nombre nombre del usuario.
     * @return lista de usuarios encontrados o un mensaje de error.
     */
    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Object> obtenerUsuariosPorNombre(@PathVariable String nombre) {
        try {
            String nombreNormalizer = nombre.toUpperCase();
            List<UsuarioResponse> usuarios = usuarioService.findByName(nombreNormalizer);
            return ResponseEntity.ok(usuarios);
        } catch (ErrorMessage ex) {
            // Devolver mensaje de error con código HTTP 404 si no se encuentran usuarios
            UsuarioResponseError error = new UsuarioResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id      identificador del usuario a actualizar.
     * @param usuarioRequest datos actualizados del usuario.
     * @return el usuario actualizado o el mensaje de error.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequest usuarioRequest) {
        try {
            return ResponseEntity.ok(usuarioService.updateUsuario(id, usuarioRequest));
        } catch (ErrorMessage ex) {
            UsuarioResponseError error = new UsuarioResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar.
     * @return una respuesta vacía con el estado correspondiente o el mensaje de
     *         error.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarUsuario(@PathVariable Long id) {
        try {
            var usuario = usuarioService.findById(id);

            if (usuario == null) {
                String mensaje = "No se encuentra usuario para eliminar";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UsuarioResponseError(mensaje));
            }
            return ResponseEntity.ok(new UsuarioResponseMessage(usuarioService.deleteUsuario(id)));
        } catch (ErrorMessage ex) {
            UsuarioResponseError error = new UsuarioResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

}
