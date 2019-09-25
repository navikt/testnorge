package no.nav.dolly.mapper.strategy;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeMedBestillingId;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class TestgruppeMedBestillingIdMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppeMedBestillingId.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppeMedBestillingId>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppeMedBestillingId testgruppeMedBestillingId, MappingContext context) {
                        testgruppeMedBestillingId.setIdenter(testgruppe.getTestidenter().stream()
                                .map(testident -> RsTestgruppeMedBestillingId.IdentBestilling.builder()
                                        .ident(testident.getIdent())
                                        .bestillingId(testident.getBestillingProgress().stream()
                                                .map(BestillingProgress::getId)
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()));
                    }
                })
                .byDefault()
                .register();
    }
}
