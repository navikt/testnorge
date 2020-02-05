package no.nav.dolly.mapper.strategy;

import static java.util.stream.Collectors.toList;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bruker.RsBrukerAndGruppeId;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class BrukerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Bruker.class, RsBrukerAndGruppeId.class)
                .customize(new CustomMapper<Bruker, RsBrukerAndGruppeId>() {
                    @Override
                    public void mapAtoB(Bruker bruker, RsBrukerAndGruppeId rsBruker, MappingContext context) {

                        if (!bruker.getFavoritter().isEmpty()) {
                            rsBruker.setFavoritter(bruker.getFavoritter().stream().map(gruppe -> gruppe.getId().toString()).collect(toList()));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
