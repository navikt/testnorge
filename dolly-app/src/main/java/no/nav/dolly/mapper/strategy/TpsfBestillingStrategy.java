package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.MapperFactory;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisBestilling;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class TpsfBestillingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsTpsfBasisBestilling.class, TpsfBestilling.class)
                .byDefault()
                .register();

        factory.classMap(RsTpsfUtvidetBestilling.class, TpsfBestilling.class)
                .byDefault()
                .register();
    }
}