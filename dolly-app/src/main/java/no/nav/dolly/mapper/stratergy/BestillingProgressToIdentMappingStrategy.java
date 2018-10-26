package no.nav.dolly.mapper.stratergy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsTestidentBestillingId;

@Component
public class BestillingProgressToIdentMappingStrategy {

    public void register(MapperFactory factory) {
        factory.classMap(BestillingProgress.class, RsTestidentBestillingId.class)
                .customize(new CustomMapper<BestillingProgress, RsTestidentBestillingId>() {
                    @Override
                    public void mapAtoB(BestillingProgress bestillingProgress, RsTestidentBestillingId rsTestidentBestillingId, MappingContext context) {
                        rsTestidentBestillingId.setIdent(bestillingProgress.getIdent());
                        rsTestidentBestillingId.setBestillingId(bestillingProgress.getBestillingId());
                    }
                })
                .register();
    }
}
