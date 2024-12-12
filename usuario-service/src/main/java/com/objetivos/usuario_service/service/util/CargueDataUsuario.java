package com.objetivos.usuario_service.service.util;

import com.objetivos.usuario_service.model.entity.Usuario;
import com.objetivos.usuario_service.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CargueDataUsuario implements CommandLineRunner {
    private final IUsuarioRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando carga usuarios");
        if (userRepository.findAll().isEmpty()) {
            userRepository.saveAll(
                    List.of(
                            Usuario.builder().nombre("Juan").apellido("Perez").email("juan@gmail.com").telefono("3213230506").build(),
                            Usuario.builder().nombre("Maria").apellido("Leguizamon").email("maria@gmail.com").telefono("3002563696").build(),
                            Usuario.builder().nombre("Mila").apellido("Jojovich").email("mila@gmail.com").telefono("3013002525").build(),
                            Usuario.builder().nombre("Diana").apellido("Bohorquez").email("diana@gmail.com").telefono("3025639698").build(),
                            Usuario.builder().nombre("Sofia").apellido("Gomez").email("").telefono("3145635685").build(),
                            Usuario.builder().nombre("John").apellido("Diaz").email("john@gmail.com").telefono("").build()
                    )
            );
        }
        log.info("Finalizando carga usuarios");
    }
}
