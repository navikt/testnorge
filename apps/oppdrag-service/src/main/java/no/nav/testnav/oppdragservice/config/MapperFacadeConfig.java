package no.nav.testnav.oppdragservice.config;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.testnav.oppdragservice.mapper.MappingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
@SuppressWarnings("java:S3740")
public class MapperFacadeConfig {

    @Bean
    MapperFacade mapperFacade(List<MappingStrategy> mappingStrategies, List<CustomConverter> customConverters) {
        var  mapperFactory = new DefaultMapperFactory.Builder().build();

        if (nonNull(mappingStrategies)) {
            for (var mapper : mappingStrategies) {
                mapper.register(mapperFactory);
            }
        }

        if (nonNull(customConverters)) {
            for (var converter : customConverters) {
                mapperFactory.getConverterFactory().registerConverter(converter);
            }
        }

        return mapperFactory.getMapperFacade();
    }
}