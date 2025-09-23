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
import static no.nav.dolly.util.TitleCaseUtil.toTitleCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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
                            nomRessursRequest.setFornavn(isNotBlank(navn.getFornavn()) ? toTitleCase(navn.getFornavn()) : null);
                            nomRessursRequest.setMellomnavn(isNotBlank(navn.getMellomnavn()) ? toTitleCase(navn.getMellomnavn()) : null);
                            nomRessursRequest.setEtternavn(isNotBlank(navn.getEtternavn()) ? toTitleCase(navn.getEtternavn()) : null);
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
