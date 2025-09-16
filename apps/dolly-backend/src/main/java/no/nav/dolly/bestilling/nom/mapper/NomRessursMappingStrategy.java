package no.nav.dolly.bestilling.nom.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

@Component
public class NomRessursMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PdlPerson.Navn.class,  NomRessursRequest.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(PdlPerson.Navn navn, NomRessursRequest nomRessursRequest, MappingContext context) {

                        nomRessursRequest.setPersonident((String) context.getProperty("ident"));
                    }
                })
                .byDefault()
                .register();
    }
}
