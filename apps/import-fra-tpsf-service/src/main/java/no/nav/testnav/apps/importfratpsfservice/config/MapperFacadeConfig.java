package no.nav.testnav.apps.importfratpsfservice.config;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.testnav.apps.importfratpsfservice.mapper.MappingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
@RequiredArgsConstructor
public class MapperFacadeConfig {

    private final List<MappingStrategy> mappingStrategies;

    @Bean
    MapperFacade mapperFacade() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (nonNull(mappingStrategies)) {
            mappingStrategies.forEach(mapper -> mapper.register(mapperFactory));
        }

        return mapperFactory.getMapperFacade();
    }
}
