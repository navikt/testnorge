package no.nav.udistub.config;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.udistub.converter.MappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Configuration
@RequiredArgsConstructor
public class MapperFacadeConfig {

    private final List<MappingStrategy> mappingStrategies;
    private final List<CustomConverter> customConverters;

    @Bean
    MapperFacade mapperFacade() {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (nonNull(mappingStrategies)) {
            mappingStrategies.forEach(mapper -> mapper.register(mapperFactory));
        }

        if (nonNull(customConverters)) {
            customConverters.forEach(converter ->
                    mapperFactory.getConverterFactory().registerConverter(converter));
        }

        return mapperFactory.getMapperFacade();
    }
}
