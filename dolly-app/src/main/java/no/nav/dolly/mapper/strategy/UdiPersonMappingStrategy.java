package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFactory;
import no.nav.dolly.domain.resultset.udistub.RsUdiPersonData;
import no.nav.dolly.domain.resultset.udistub.model.PersonTo;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class UdiPersonMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsUdiPersonData.class, PersonTo.class)
                .byDefault()
                .register();
    }
}
