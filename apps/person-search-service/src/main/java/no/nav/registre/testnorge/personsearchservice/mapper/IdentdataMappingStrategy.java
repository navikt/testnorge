package no.nav.registre.testnorge.personsearchservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.personsearchservice.adapter.model.IdenterModel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.NavnModel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.testnav.libs.dto.personsearchservice.v1.IdentdataDTO;
import no.nav.testnav.libs.dto.personsearchservice.v1.NavnDTO;
import org.springframework.stereotype.Component;

@Component
public class IdentdataMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Response.class, IdentdataDTO.class)
                .customize(new CustomMapper<Response, IdentdataDTO>() {
                    @Override
                    public void mapAtoB(Response response, IdentdataDTO identdataDTO, MappingContext context) {

                        identdataDTO.setIdent(response.getHentIdenter().getIdenter().stream()
                                .filter(identer -> "FOLKEREGISTERIDENT".equals(identer.getGruppe()))
                                .map(IdenterModel::getIdent)
                                .findFirst().orElse(null));

                        identdataDTO.setNavn(mapperFacade.map(response.getHentPerson().getNavn().stream()
                                .findFirst().orElse(new NavnModel()), NavnDTO.class));
                    }
                })
                .register();
    }
}
