package no.nav.dolly.mapper.strategy;

import java.util.Arrays;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BestillingMappingStrategy implements MappingStrategy{
    @Override public void register(MapperFactory factory) {
        factory.classMap(Bestilling.class, RsBestilling.class)
                .customize(new CustomMapper<Bestilling, RsBestilling>() {
                    @Override public void mapAtoB(Bestilling bestilling, RsBestilling rsBestilling, MappingContext context) {
                        rsBestilling.setId(bestilling.getId());
                        rsBestilling.setAntallIdenter(bestilling.getAntallIdenter());
                        rsBestilling.setFerdig(bestilling.isFerdig());
                        rsBestilling.setEnvironments(Arrays.asList(bestilling.getMiljoer().split(",")));
                        rsBestilling.setSistOppdatert(bestilling.getSistOppdatert());
                        rsBestilling.setGruppeId(bestilling.getGruppe().getId());
                    }
                })
                .register();
    }
}
