package no.nav.dolly.mapper.utils;

import static java.util.Objects.nonNull;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import no.nav.dolly.mapper.MappingStrategy;

public class MapperTestUtils {

    public static MapperFacade createMapperFacadeForMappingStrategy(MappingStrategy... strategies) {
        return createMapperFacadeForMappingStrategy(null, strategies);
    }

    public static MapperFacade createMapperFacadeForMappingStrategy(CustomConverter converter, MappingStrategy... strategies) {
        DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        for (MappingStrategy strategy : strategies) {
            strategy.register(mapperFactory);
        }

        if (nonNull(converter)) {
            mapperFactory.getConverterFactory().registerConverter(converter);
        }
        return mapperFactory.getMapperFacade();
    }
}
