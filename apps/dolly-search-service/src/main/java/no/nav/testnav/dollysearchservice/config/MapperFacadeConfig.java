package no.nav.testnav.dollysearchservice.config;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.testnav.dollysearchservice.mapper.MappingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Objects.nonNull;

@Configuration
@SuppressWarnings("java:S3740")
public class MapperFacadeConfig {

    @Bean
    MapperFacade mapperFacade(List<MappingStrategy> mappingStrategies, List<CustomConverter> customConverters) {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        if (nonNull(mappingStrategies)) {
            for (MappingStrategy mapper : mappingStrategies) {
                mapper.register(mapperFactory);
            }
        }

        if (nonNull(customConverters)) {
            for (CustomConverter converter : customConverters) {
                mapperFactory.getConverterFactory().registerConverter(converter);
            }
        }

        return mapperFactory.getMapperFacade();
    }
}
