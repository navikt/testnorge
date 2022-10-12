package no.nav.dolly.mapper.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsMalBestillingWrapper;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static no.nav.dolly.bestilling.service.DollyBestillingService.getEnvironments;

@Slf4j
@Component
@RequiredArgsConstructor
public class MalBestillingMappingStrategy implements MappingStrategy {

    private final JsonBestillingMapper jsonBestillingMapper;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsMalBestillingWrapper.RsBestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bestilling bestilling, RsMalBestillingWrapper.RsBestilling malBestilling, MappingContext context) {

                        RsDollyBestillingRequest bestillingRequest = jsonBestillingMapper.mapBestillingRequest(bestilling.getBestKriterier());
                        mapperFacade.map(bestillingRequest, malBestilling);
                        malBestilling.setEnvironments(getEnvironments(bestilling.getMiljoer()));
                        malBestilling.setTpsf(jsonBestillingMapper.mapTpsfRequest(bestilling.getTpsfKriterier()));
                        malBestilling.setNavSyntetiskIdent(bestilling.getNavSyntetiskIdent());
                    }
                })
                .byDefault()
                .register();
    }
}
