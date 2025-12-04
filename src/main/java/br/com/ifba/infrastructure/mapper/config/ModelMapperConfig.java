package br.com.ifba.infrastructure.mapper.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(
                        // Se eu tento importar dá conflito com o import 'org.springframework.context.annotation.Configuration;'
                        org.modelmapper.config.Configuration.AccessLevel.PRIVATE
                );

        return modelMapper;
    }
}

