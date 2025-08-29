package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class TestgruppeMedBestillingIdMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppeMedBestillingId.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppeMedBestillingId testgruppeMedBestillingId, MappingContext context) {
                        testgruppeMedBestillingId.setIdenter(testgruppe.getTestidenter().stream()
                                .map(testident -> RsTestgruppeMedBestillingId.IdentBestilling.builder()
                                        .ident(testident.getIdent())
                                        .iBruk(isTrue(testident.getIBruk()))
                                        .beskrivelse(testident.getBeskrivelse())
                                        .bestillingId(testident.getBestillingProgress().stream()
                                                .map(BestillingProgress::getBestilling)
                                                .filter(bestilling ->
                                                        bestilling.getGruppe().getId().equals(testident.getTestgruppe().getId()))
                                                .map(Bestilling::getId)
                                                .sorted(Comparator.reverseOrder())
                                                .toList())
                                        .master(testident.getMaster())
                                        .bestillinger(testident.getBestillingProgress().stream()
                                                .map(BestillingProgress::getBestilling)
                                                .filter(bestilling ->
                                                        bestilling.getGruppe().getId().equals(testident.getTestgruppe().getId()))
                                                .map(bestilling -> {
                                                    var context2 = new MappingContext.Factory().getContext();
                                                    context2.setProperty("ident", testident.getIdent());
                                                    var status = mapperFacade.map(bestilling, RsBestillingStatus.class, context2);
                                                    return RsBestillingStatus.builder()
                                                            .id(bestilling.getId())
                                                            .status(status.getStatus())
                                                            .bestilling(status.getBestilling())
                                                            .build();
                                                })
                                                .toList())
                                        .build())
                                .toList());
                        testgruppeMedBestillingId.setErLaast(isTrue(testgruppe.getErLaast()));
                    }
                })
                .register();
    }
}
