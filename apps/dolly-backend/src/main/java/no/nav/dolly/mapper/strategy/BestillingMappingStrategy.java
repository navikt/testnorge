package no.nav.dolly.mapper.strategy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class BestillingMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, Bestilling.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Bestilling kilde, Bestilling destinasjon, MappingContext context) {

                        destinasjon.setFerdig(true);
                        destinasjon.setBestKriterier(kilde.getBestKriterier());
                        destinasjon.setBruker(kilde.getBruker());
                    }
                })
                .byDefault()
                .exclude("progresser")
                .register();

        factory.classMap(BestillingProgress.class, BestillingProgress.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(BestillingProgress kilde, BestillingProgress destinasjon, MappingContext context) {

                    }
                })
                .byDefault()
                .exclude("bestilling")
                .register();
    }
}
