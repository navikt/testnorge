package no.nav.mapper.stratergy;

import ma.glasnost.orika.MapperFactory;
import no.nav.api.resultSet.RsTestgruppe;
import no.nav.jpa.Testgruppe;
import no.nav.mapper.MappingStrategy;

import org.springframework.stereotype.Component;

@Component
public class TestgruppeMappingStratergy implements MappingStrategy{

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppe.class)
                .byDefault()
                .register();

        factory.classMap(RsTestgruppe.class, Testgruppe.class)
                .byDefault()
                .register();
    }
}
