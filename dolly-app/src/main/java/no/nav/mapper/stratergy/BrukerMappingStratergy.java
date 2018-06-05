package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFactory;
import no.nav.api.resultSet.RsBruker;
import no.nav.jpa.Bruker;
import no.nav.mapper.MappingStrategy;

import org.springframework.stereotype.Component;

@Component
public class BrukerMappingStratergy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBruker.class)
                .byDefault()
                .register();
    }
}
