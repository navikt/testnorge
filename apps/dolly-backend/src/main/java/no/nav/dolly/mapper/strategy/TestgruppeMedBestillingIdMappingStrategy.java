package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class TestgruppeMedBestillingIdMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Testgruppe.class, RsTestgruppeMedBestillingId.class)
                .customize(new CustomMapper<Testgruppe, RsTestgruppeMedBestillingId>() {
                    @Override
                    public void mapAtoB(Testgruppe testgruppe, RsTestgruppeMedBestillingId testgruppeMedBestillingId, MappingContext context) {
                        testgruppeMedBestillingId.setIdenter(testgruppe.getTestidenter().stream()
                                .map(testident -> RsTestgruppeMedBestillingId.IdentBestilling.builder()
                                        .ident(testident.getIdent())
                                        .iBruk(isTrue(testident.getIBruk()))
                                        .beskrivelse(testident.getBeskrivelse())
                                        .bestillingId(testident.getBestillingProgress().stream()
                                                .filter(bestillingProgress ->
                                                        bestillingProgress.getBestilling().getGruppe().getId().equals(testident.getTestgruppe().getId()))
                                                .map(BestillingProgress::getBestilling)
                                                .map(Bestilling::getId)
                                                .sorted(Comparator.reverseOrder())
                                                .collect(Collectors.toList()))
                                        .master(testident.getMaster())
                                        .build())
                                .collect(Collectors.toList()));
                        testgruppeMedBestillingId.setErLaast(isTrue(testgruppe.getErLaast()));
                    }
                })
                .register();
    }
}
