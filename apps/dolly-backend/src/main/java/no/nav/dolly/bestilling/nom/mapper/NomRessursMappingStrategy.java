package no.nav.dolly.bestilling.nom.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.nom.domain.NomRessursRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.resultset.RsNomData;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class NomRessursMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsNomData.class,  NomRessursRequest.class)
                .customize(new CustomMapper<>() {

                    @Override
                    public void mapAtoB(RsNomData nomdata, NomRessursRequest nomRessursRequest, MappingContext context) {

                        nomRessursRequest.setPersonident((String) context.getProperty("ident"));

                        var navn = (PdlPerson.Navn) context.getProperty("navn");
                        if (nonNull(navn)) {
                            nomRessursRequest.setFornavn(navn.getFornavn());
                            nomRessursRequest.setMellomnavn(navn.getMellomnavn());
                            nomRessursRequest.setEtternavn(navn.getEtternavn());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
