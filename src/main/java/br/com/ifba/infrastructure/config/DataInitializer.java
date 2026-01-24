package br.com.ifba.infrastructure.config;

import br.com.ifba.usuario.entity.TipoUsuario;
import br.com.ifba.usuario.repository.TipoUsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(TipoUsuarioRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new TipoUsuario("ADMIN", "Administrador"));
                repository.save(new TipoUsuario("COMUM", "Usuário Comum"));
                System.out.println("Tipos de usuário inicializados com sucesso!");
            }
        };
    }
}
