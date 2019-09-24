package no.nav.dolly.mapper.strategy;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.udistub.RsAliasResponse;
import no.nav.dolly.domain.resultset.udistub.model.UdiAlias;
import no.nav.dolly.domain.resultset.udistub.model.UdiPersonNavn;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class UdiAliasMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAliasResponse.Persondata.class, UdiAlias.class)
                .customize(new CustomMapper<RsAliasResponse.Persondata, UdiAlias>() {
                    @Override public void mapAtoB(RsAliasResponse.Persondata persondata, UdiAlias udiAlias, MappingContext context) {

                        udiAlias.setFnr(persondata.getIdent());
                        udiAlias.setNavn(mapperFacade.map(udiAlias.getNavn(), UdiPersonNavn.class));
                    }
                })
                .byDefault()
                .register();
    }
}