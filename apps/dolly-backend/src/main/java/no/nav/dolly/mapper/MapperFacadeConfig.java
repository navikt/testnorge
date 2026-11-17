package no.nav.dolly.mapper;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MapperFacadeConfig {

    @Bean
    MapperFacade mapperFacade(List<MappingStrategy> mappingStrategies, List<CustomConverter> customConverters) {

        var mapperFactory = new DefaultMapperFactory.Builder()
                .mappingContextFactory(MappingContextUtils.getMappingContextFactory())
                .build();

        for (var converter : customConverters) {
            mapperFactory.getConverterFactory().registerConverter(converter);
        }

        for (var strategy : mappingStrategies) {
            strategy.register(mapperFactory);
        }

        return mapperFactory.getMapperFacade();
    }
}
