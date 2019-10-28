package no.nav.dolly.mapper.strategy;

import static java.util.Objects.nonNull;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBruker;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BrukerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBruker.class)
                .customize(new CustomMapper<Bruker, RsBruker>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBruker rsBruker, MappingContext context) {
                        rsBruker.setNavIdent(bruker.getBrukerId());

                        if (nonNull(bruker.getFavoritter())) {
                            rsBruker.setFavoritter(mapperFacade.mapAsList(bruker.getFavoritter(), RsTestgruppe.class));
                        }
                    }
                })
                .register();
    }
}
