package no.nav.dolly.mapper.stratergy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsTestidentBestillingId;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class TestIdentMappingStrategy implements MappingStrategy  {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Testident.class, RsTestidentBestillingId.class)
                .customize(new CustomMapper<Testident, RsTestidentBestillingId>() {
                    @Override
                    public void mapAtoB(Testident testgruppe, RsTestidentBestillingId rsTestgruppeBestillingId, MappingContext context) {
                        rsTestgruppeBestillingId.setBestillingId(testgruppe.getBestillingProgress().get(0).getBestillingId());
                    }
                })
                .byDefault()
                .register();
    }
}
