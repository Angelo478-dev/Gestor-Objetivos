package com.objetivos.objetivo_service.service;

import com.objetivos.objetivo_service.model.dto.ObjetivoRequest;
import com.objetivos.objetivo_service.model.dto.ObjetivoResponse;
import com.objetivos.objetivo_service.model.dto.UsuarioDTO;
import com.objetivos.objetivo_service.model.entity.Objetivo;
import com.objetivos.objetivo_service.repository.IObjetivoRepository;
import com.objetivos.objetivo_service.repository.IUsuarioFeignClient;
import com.objetivos.objetivo_service.service.Util.DateValidator;
import com.objetivos.objetivo_service.service.Util.ErrorMessage;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ObjetivoService {

    private final IObjetivoRepository objetivoRepository;
    @Autowired
    private final IUsuarioFeignClient usuarioFeignClient;

    // Constructor para inyección de dependencias
    public ObjetivoService(IObjetivoRepository objetivoRepository, IUsuarioFeignClient usuarioFeignClient) {
        this.objetivoRepository = objetivoRepository;
        this.usuarioFeignClient = usuarioFeignClient;
    }
    
    /* Metodos publicos */

    /**
     * Método para consultar todos los objetivos.
     * 
     * @return Lista de objetivos encontrados.
     * @throws ErrorMessage si no se encuentran objetivos en la base de datos.
     */

    public List<ObjetivoResponse> getAllObjetivos() {
        List<Objetivo> objetivos = objetivoRepository.findAll();
        if (objetivos.isEmpty()) {
            throw new ErrorMessage("No se encontraron objetivos");
        }
        return objetivos.stream()
                .map(this::mapToObjetivoResponse)
                .toList();
    }

    /**
     * Método para encontrar un objetivo por su ID.
     * 
     * @param id identificador único del objetivo.
     * @return ObjetivoResponse con los detalles del objetivo encontrado.
     * @throws ErrorMessage si no se encuentra un objetivo con ese ID.
     */
    public ObjetivoResponse findById(Long id) {
        var objetivo = objetivoRepository.findById(id).orElse(null);
        if (objetivo == null) {
            log.error("No se encontro objetivo: {}", id);
            throw new ErrorMessage("No se encontro objetivo:" + id);
        }
        return mapToObjetivoResponse(objetivo);
    }

    /**
     * Método para buscar objetivos por título.
     * 
     * @param titulo título del objetivo a buscar.
     * @return Lista de ObjetivoResponse que contienen los objetivos con el título proporcionado.
     */
    public List<ObjetivoResponse> findByTitulo(String titulo) {
        List<Objetivo> objetivos = objetivoRepository.findByTitulo(titulo);
        if (objetivos.isEmpty()) {
            log.error("No se encontro objetivo con titulo: {}", titulo);
            throw new ErrorMessage("No se encontro objetivo:" + titulo);
        }
        return objetivos.stream()
                .map(this::mapToObjetivoResponse)
                .collect(Collectors.toList());
    }

    /**
     * Método para crear un nuevo objetivo.
     * 
     * @param objetivoRequest objeto que contiene la información para crear un nuevo objetivo.
     * @return Objetivo recién creado.
     */
    public Objetivo createObjetivo(ObjetivoRequest objetivoRequest) {
        try {
            // Validación del request
            validateObjetivoRequest(objetivoRequest);

            // Validación del usuario
            if (!validarUsuario(objetivoRequest.getUsuarioId())) {
                log.warn("El usuario con ID {} no es válido. No se puede crear el objetivo.",
                        objetivoRequest.getUsuarioId());
                return null; // o puedes lanzar una excepción personalizada
            }

            LocalDate fechaLimite = parseFechaLimite(objetivoRequest.getFechaLimite());

            // Creación del objetivo
            Objetivo objetivo = Objetivo.builder()
                    .titulo(objetivoRequest.getTitulo().toUpperCase())
                    .descripcion(objetivoRequest.getDescripcion().toUpperCase())
                    .fechaLimite(fechaLimite)
                    .usuarioId(objetivoRequest.getUsuarioId())
                    .build();

            log.info("Creando objetivo con título: {} y descripcion: {}", objetivo.getTitulo(),
                    objetivo.getDescripcion());
            return objetivoRepository.save(objetivo);

        } catch (IllegalArgumentException e) {
            log.error("Error de validación al crear objetivo: {}", e.getMessage(), e);
            throw e; // Re-lanza la excepción si es necesario
        } catch (Exception e) {
            log.error("Error inesperado al crear objetivo: {}", e.getMessage(), e);
            throw new RuntimeException("Error al crear el objetivo", e); // Lanza una excepción general o personalizada
        }
    }

   /**
     * Método para actualizar un objetivo existente.
     * 
     * @param id identificador del objetivo a actualizar.
     * @param objetivoRequest objeto que contiene los datos actualizados del objetivo.
     * @return Objetivo actualizado.
     * @throws ErrorMessage si el objetivo no existe para actualizar.
     */
    public Objetivo updateObjetivo(Long id, ObjetivoRequest objetivoRequest) {
        // Buscar el objetivo existente
        Objetivo objectObjetivo = objetivoRepository.findById(id).orElse(null);
        if (objectObjetivo == null) {
            log.error("No se puede actualizar, no se encuentra objetivo con ID: {}", id);
            throw new ErrorMessage("No se puede actualizar un objetivo inexistente.");
        }
    
        // Actualizar campos principales
        objectObjetivo.setTitulo(objetivoRequest.getTitulo().toUpperCase());
        objectObjetivo.setDescripcion(objetivoRequest.getDescripcion().toUpperCase());
    
        // Actualizar estado de completado
        boolean completado = objetivoRequest.getCompletado() == 1 ? true : false;
        objectObjetivo.setCompletado(completado);
    
        log.info("Objetivo con ID {} actualizado exitosamente", id);
        return objetivoRepository.save(objectObjetivo);
    }    


    /**
     * Método para eliminar un objetivo por su ID.
     * 
     * @param id identificador del objetivo a eliminar.
     * @return Mensaje indicando si la eliminación fue exitosa.
     * @throws ErrorMessage si no se encuentra el objetivo para eliminar.
     */
    public String deleteObjetivo(Long id) {
        if (!objetivoRepository.existsById(id)) {
            log.error("No se puede eliminar, el objetivo no existe con el ID:{}", id);
            throw new ErrorMessage("No se puede eliminar, el objetivo no existe con el ID: " + id);
        }
        objetivoRepository.deleteById(id);
        log.info("Objetivo con ID {} eliminado exitosamente", id);

        return String.format("Objetivo %s eliminado exitosamente", id);
    }

    /**
     * Método privado para mapear un objeto de tipo Objetivo a ObjetivoResponse.
     * 
     * @param objetivo objeto de tipo Objetivo a mapear.
     * @return ObjetivoResponse con los detalles del objetivo.
     */
    private ObjetivoResponse mapToObjetivoResponse(Objetivo objetivo) {
        ObjetivoResponse response = new ObjetivoResponse();
        response.setId(objetivo.getId());
        response.setTitulo(objetivo.getTitulo());
        response.setDescripcion(objetivo.getDescripcion());
        response.setFechaLimite(objetivo.getFechaLimite());
        response.setCompletado(objetivo.getCompletado() == false ? "Pendiente" : "Completado");
        response.setUsuarioId(objetivo.getUsuarioId());

        try {
            // Llamada al servicio de usuario para obtener el nombre
            UsuarioDTO usuario = usuarioFeignClient.obtenerUsuarioPorId(objetivo.getUsuarioId());
            response.setUsuarioNombre(usuario.getNombre());
        } catch (FeignException.NotFound e) {
            log.warn("Usuario con ID {} no encontrado. No se puede mapear el nombre.", objetivo.getUsuarioId());
            response.setUsuarioNombre("Usuario no encontrado");
        } catch (Exception e) {
            log.error("Error al obtener el nombre del usuario con ID {}: {}", objetivo.getUsuarioId(), e.getMessage());
            response.setUsuarioNombre("Error al obtener nombre");
        }

        return response;
    }

    /**
     * Método para validar los datos del request al crear o actualizar un objetivo.
     * 
     * @param objetivoRequest objeto con los datos del objetivo a validar.
     * @throws IllegalArgumentException si algún campo es inválido.
     */
    private void validateObjetivoRequest(ObjetivoRequest objetivoRequest) {
        if (objetivoRequest.getTitulo() == null || objetivoRequest.getTitulo().isEmpty()
                || objetivoRequest.getTitulo() == "null") {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        if (objetivoRequest.getDescripcion() == null || objetivoRequest.getDescripcion().isEmpty()
                || objetivoRequest.getDescripcion() == "null") {
            throw new IllegalArgumentException("La descripción no puede estar vacía.");
        }
        if (objetivoRequest.getFechaLimite() == null) {
            throw new IllegalArgumentException("La fecha límite es obligatoria.");
        }
        if (objetivoRequest.getUsuarioId() == null) {
            throw new IllegalArgumentException("El id usuario es obligatorio.");
        }
        if (!DateValidator.isValidDate(objetivoRequest.getFechaLimite(), "yyyy-MM-dd")) {
            throw new IllegalArgumentException("El formato de la fecha debe ser 'yyyy-MM-dd'.");
        }
    }

    /**
     * Método para parsear la fecha límite del objetivo.
     * 
     * @param fecha fecha en formato String.
     * @return LocalDate con la fecha límite parseada.
     */
    public LocalDate parseFechaLimite(String fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(fecha, formatter);
  
    }

    /**
     * Método para validar si el usuario existe
     * @param usuarioId
     * @return 
     */
    
    private boolean validarUsuario(Long usuarioId) {
        try {
            UsuarioDTO usuario = usuarioFeignClient.obtenerUsuarioPorId(usuarioId);
            return usuario != null;
        } catch (Exception e) {
            log.error(String.format("Error al obtener el usuario con ID %s: %s", usuarioId, e.getMessage()), e);
            return false;
        }
    }

    /* Metodos privados */
}
