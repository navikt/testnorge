package no.nav.registre.aareg.testutils;

import static java.util.Objects.nonNull;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import no.nav.registre.aareg.config.MappingStrategy;

public class MapperTestUtils {

    public static MapperFacade createMapperFacadeForMappingStrategy(CustomConverter converter, MappingStrategy... strategies) {
        var mapperFactory = new DefaultMapperFactory.Builder().build();

        for (var strategy : strategies) {
            strategy.register(mapperFactory);
        }

        if (nonNull(converter)) {
            mapperFactory.getConverterFactory().registerConverter(converter);
        }
        return mapperFactory.getMapperFacade();
    }
}
