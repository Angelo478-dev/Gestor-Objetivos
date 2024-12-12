package com.objetivos.objetivo_service.controller;

import com.objetivos.objetivo_service.model.dto.ObjetivoRequest;
import com.objetivos.objetivo_service.model.dto.ObjetivoResponse;
import com.objetivos.objetivo_service.model.dto.ObjetivoResponseError;
import com.objetivos.objetivo_service.model.dto.ObjetivoResponseMessage;
import com.objetivos.objetivo_service.model.dto.UsuarioDTO;
import com.objetivos.objetivo_service.model.entity.Objetivo;
import com.objetivos.objetivo_service.repository.IUsuarioFeignClient;
import com.objetivos.objetivo_service.service.ObjetivoService;
import com.objetivos.objetivo_service.service.UsuarioService;
import com.objetivos.objetivo_service.service.Util.ErrorMessage;
import feign.FeignException;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/objetivos")
public class ObjetivoController {

    @Autowired
    private ObjetivoService objetivoService;
    private final IUsuarioFeignClient usuarioFeignClient;

    @Autowired
    UsuarioService usuarioService;

    public ObjetivoController(IUsuarioFeignClient usuarioFeignClient,
            ObjetivoService objetivoService) {
        this.usuarioFeignClient = usuarioFeignClient;
        this.objetivoService = objetivoService;
    }

    // Endpoint para obtener la lista de usuarios desde el servicio remoto
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDTO>> obtenerUsuarios() {
        try {
            List<UsuarioDTO> usuarios = usuarioFeignClient.obtenerUsuarios();
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    // Endpoint para obtener un usuario por ID
    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDTO> obtenerUsuarioPorId(@PathVariable("id") Long id) {
        try {
            // Llamada al Feign Client para obtener el usuario desde usuario-service
            UsuarioDTO usuario = usuarioFeignClient.obtenerUsuarioPorId(id);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Si no se encuentra el usuario
            }

            return ResponseEntity.ok(usuario); // Usuario encontrado
        } catch (FeignException.NotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Maneja el error 404 si el usuario no se
                                                                           // encuentra
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Otros errores
        }
    }

    /**
     * Obtener todos los objetivos
     */
    @GetMapping
    public ResponseEntity<Object> getAllObjetivos() {
        try {
            var objetivos = objetivoService.getAllObjetivos();
            return ResponseEntity.ok(objetivos);
        } catch (ErrorMessage ex) {
            var error = new ObjetivoResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Obtener objetivo por ID
     */
    @GetMapping("/detalle/{id}")
    public ResponseEntity<Object> getObjetivoById(@PathVariable Long id) {
        try {
            var objetivo = objetivoService.findById(id);
            return ResponseEntity.ok(objetivo);
        } catch (ErrorMessage ex) {
            var error = new ObjetivoResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Buscar objetivos por título
     */
    @GetMapping("/titulo/{titulo}")
    public ResponseEntity<Object> getObjetivosByTitulo(@PathVariable String titulo) {
        try {
            String tituloNormalizer = titulo.toUpperCase();
            List<ObjetivoResponse> objetivos = objetivoService.findByTitulo(tituloNormalizer);
            return ResponseEntity.ok(objetivos);
        } catch (ErrorMessage ex) {
            var error = new ObjetivoResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Crear un objetivo
     */
    @PostMapping
    public ResponseEntity<Object> createObjetivo(@Validated @RequestBody ObjetivoRequest objetivoRequest) {
        try {
            // Llamada al servicio para crear el objetivo
            Objetivo objetivo = objetivoService.createObjetivo(objetivoRequest);

            //log.info("objetivo: "+objetivo);
            if (objetivo == null) {
                String mensaje = "No se encuentra usuario con ID " + objetivoRequest.getUsuarioId() + 
                             ". Por favor, verifique o cree un usuario.";
                             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjetivoResponseMessage(mensaje));
            }
            return ResponseEntity.ok(objetivo);

        } catch (FeignException.NotFound ex) {
            // Manejar el caso específico de usuario no encontrado
            String mensaje = "No se encuentra usuario con ID " + objetivoRequest.getUsuarioId() + 
                             ". Por favor, verifique o cree un usuario.";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ObjetivoResponseMessage(mensaje));

        } catch (Exception ex) {
            // Manejar otros errores inesperados
            String mensajeError = "Ha ocurrido un error inesperado al crear el objetivo: " + ex.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ObjetivoResponseError(mensajeError));
        }
    }    

    /**
     * Actualizar un objetivo
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateObjetivo(@PathVariable Long id, @RequestBody ObjetivoRequest objetivoRequest) {
        try {
            return ResponseEntity.ok(objetivoService.updateObjetivo(id, objetivoRequest));
        } catch (ErrorMessage ex) {
            var error = new ObjetivoResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Eliminar un objetivo
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteObjetivo(@PathVariable Long id) {
        try {
            // Busca el objetivo por ID
            var objetivo = objetivoService.findById(id);

            // Verifica si el objetivo existe
            if (objetivo == null) {
                String mensaje = "No se encuentra objetivo para eliminar";
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ObjetivoResponseMessage(mensaje));
            }

            // Elimina el objetivo
            objetivoService.deleteObjetivo(id);
            return ResponseEntity.ok(new ObjetivoResponseMessage("Objetivo eliminado exitosamente"));

        } catch (ErrorMessage ex) {
            // Manejo de errores personalizados
            var error = new ObjetivoResponseError(ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

}
