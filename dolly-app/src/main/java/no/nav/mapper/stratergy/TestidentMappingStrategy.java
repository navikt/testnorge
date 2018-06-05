package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFactory;
import no.nav.api.resultSet.RsTestident;
import no.nav.jpa.Testident;
import no.nav.mapper.MappingStrategy;

public class TestidentMappingStrategy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testident.class, RsTestident.class)
                .byDefault()
                .register();
    }
}
