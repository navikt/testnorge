package no.nav.dolly.mapper.strategy;

import static no.nav.dolly.mapper.BestillingAaregStatusMapper.buildAaregStatusMap;
import static no.nav.dolly.mapper.BestillingArenaforvalterStatusMapper.buildArenaStatusMap;
import static no.nav.dolly.mapper.BestillingInstdataStatusMapper.buildInstdataStatusMap;
import static no.nav.dolly.mapper.BestillingKrrStubStatusMapper.buildKrrStubStatusMap;
import static no.nav.dolly.mapper.BestillingPdlForvalterStatusMapper.buildPdldataStatusMap;
import static no.nav.dolly.mapper.BestillingSigrunStubStatusMapper.buildSigrunStubStatusMap;
import static no.nav.dolly.mapper.BestillingTpsfStatusMapper.buildTpsfStatusMap;
import static no.nav.dolly.mapper.BestillingUdiStubStatusMapper.buildUdiStubStatusMap;

import java.util.Arrays;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.mapper.MappingStrategy;

@Deprecated
@Component
public class BestillingMappingStrategy implements MappingStrategy {

    @Override public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestilling.class)
                .customize(new CustomMapper<Bestilling, RsBestilling>() {
                    @Override public void mapAtoB(Bestilling bestilling, RsBestilling rsBestilling, MappingContext context) {
                        rsBestilling.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        rsBestilling.setGruppeId(bestilling.getGruppe().getId());
                        rsBestilling.getTpsfStatus().addAll(buildTpsfStatusMap(bestilling.getProgresser()));
                        rsBestilling.getKrrStubStatus().addAll(buildKrrStubStatusMap(bestilling.getProgresser()));
                        rsBestilling.getSigrunStubStatus().addAll(buildSigrunStubStatusMap(bestilling.getProgresser()));
                        rsBestilling.getUdiStubStatus().addAll(buildUdiStubStatusMap(bestilling.getProgresser()));
                        rsBestilling.getAaregStatus().addAll(buildAaregStatusMap(bestilling.getProgresser()));
                        rsBestilling.getArenaforvalterStatus().addAll(buildArenaStatusMap(bestilling.getProgresser()));
                        rsBestilling.setPdlforvalterStatus(buildPdldataStatusMap(bestilling.getProgresser()));
                        rsBestilling.getInstdataStatus().addAll(buildInstdataStatusMap(bestilling.getProgresser()));
                    }
                })
                .byDefault()
                .register();
    }
}
