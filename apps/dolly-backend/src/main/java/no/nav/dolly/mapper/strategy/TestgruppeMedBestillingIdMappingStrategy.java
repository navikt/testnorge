package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Comparator;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class TestgruppeMedBestillingIdMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsTestgruppe.class, RsTestgruppeMedBestillingId.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsTestgruppe testgruppe, RsTestgruppeMedBestillingId rsTestgruppe, MappingContext context) {

                        var identer = (Page<Testident>) context.getProperty("identer");

                        rsTestgruppe.setIdenter(identer.getContent().stream()
                                .map(testident -> RsTestgruppeMedBestillingId.IdentBestilling.builder()
                                        .ident(testident.getIdent())
                                        .iBruk(isTrue(testident.getIBruk()))
                                        .beskrivelse(testident.getBeskrivelse())
                                        .bestillingId(nonNull(testident.getBestillingProgress()) ? testident.getBestillingProgress().stream()
                                                .map(BestillingProgress::getBestillingId)
                                                .sorted(Comparator.reverseOrder())
                                                .toList() : null)
                                        .master(testident.getMaster())
                                        .bestillinger(nonNull(testident.getBestillingProgress()) ? testident.getBestillingProgress().stream()
                                                .map(progress -> {
                                                    var context2 = MappingContextUtils.getMappingContext();
                                                    context2.setProperty("ident", testident.getIdent());
                                                    return mapperFacade.map(progress.getBestilling(), RsBestillingStatus.class, context2);
                                                })
                                                .toList() : null)
                                        .master(testident.getMaster())
                                        .build())
                                .toList());
                    }
                })
                .byDefault()
                .register();
    }
}
