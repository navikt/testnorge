package no.nav.mapper.stratergy;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.resultSet.RsBestilling;
import no.nav.jpa.Bestilling;
import no.nav.mapper.MappingStrategy;

import java.util.Arrays;
import org.springframework.stereotype.Component;

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
