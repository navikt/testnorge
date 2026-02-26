package no.nav.testnav.oppdragservice.mapper;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import java.util.List;

import static java.util.Objects.nonNull;

public class MapperTestUtils {

    public static MapperFacade createMapperFacadeForMappingStrategy(MappingStrategy... strategies) {
        return createMapperFacadeForMappingStrategy(null, strategies);
    }

    public static MapperFacade createMapperFacadeForMappingStrategy(List<CustomConverter> converters, MappingStrategy... strategies) {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        for (MappingStrategy strategy : strategies) {
            strategy.register(mapperFactory);
        }

        if (nonNull(converters)) {
            converters
                    .forEach(converter -> mapperFactory.getConverterFactory().registerConverter(converter));
        }
        return mapperFactory.getMapperFacade();
    }
}