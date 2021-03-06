package no.nav.organisasjonforvalter.config;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.organisasjonforvalter.mapper.MappingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
public class MapperFacadeConfig {

    @Bean
    MapperFacade mapperFacade(List<MappingStrategy> mappingStrategies) {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (nonNull(mappingStrategies)) {
            for (MappingStrategy mapper : mappingStrategies) {
                mapper.register(mapperFactory);
            }
        }

        return mapperFactory.getMapperFacade();
    }
}