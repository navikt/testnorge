package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.mapper.BestillingAaregStatusMapperNy.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapperNy.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapperNy.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingKrrStubStatusMapperNy.buildKrrStubStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapperNy.buildPdldataStatusMap;
import static no.nav.dolly.mapper.BestillingSigrunStubStatusMapperNy.buildSigrunStubStatusMap;
import static no.nav.dolly.mapper.BestillingTpsfStatusMapperNy.buildTpsfStatusMap;
import static no.nav.dolly.mapper.BestillingUdiStubStatusMapperNy.buildUdiStubStatusMap;

import java.util.Arrays;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BestillingStatusMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestillingStatus.class)
                .customize(new CustomMapper<Bestilling, RsBestillingStatus>() {
                    @Override public void mapAtoB(Bestilling bestilling, RsBestillingStatus bestillingStatus, MappingContext context) {
                        bestillingStatus.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        bestillingStatus.setGruppeId(bestilling.getGruppe().getId());
                        bestillingStatus.getStatus().addAll(buildTpsfStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildKrrStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildSigrunStubStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildAaregStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildArenaStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildPdldataStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildInstdataStatusMap(bestilling.getProgresser()));
                        bestillingStatus.getStatus().addAll(buildUdiStubStatusMap(bestilling.getProgresser()));
                    }
                })
                .byDefault()
                .register();
    }
}
