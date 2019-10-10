package no.nav.dolly.mapper.strategy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.RsTestidentBestillingId;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class TestgruppeUtvidetMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppeUtvidet.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppeUtvidet>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppeUtvidet testgruppeUtvidet, MappingContext context) {
                        testgruppeUtvidet.setTestidenter(mapperFacade.mapAsList(testgruppe.getTestidenter(), RsTestidentBestillingId.class));
                    }
                })
                .byDefault()
                .register();
    }
}