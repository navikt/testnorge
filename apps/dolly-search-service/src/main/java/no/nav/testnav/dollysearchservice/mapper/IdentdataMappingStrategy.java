package no.nav.testnav.dollysearchservice.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.testnav.dollysearchservice.dto.Person;
import no.nav.testnav.libs.dto.dollysearchservice.v1.IdentdataDTO;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IdentdataMappingStrategy implements MappingStrategy {

    private final ObjectMapper objectMapper;
    @Override
    public void register(MapperFactory factory) {

        factory.classMap(JsonNode.class, IdentdataDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(JsonNode response, IdentdataDTO identdataDTO, MappingContext context) {

                        var person = objectMapper.convertValue(response, Person.class);
                        identdataDTO.setIdent(person.getHentIdenter().getIdenter().stream()
                                .filter(identer -> "FOLKEREGISTERIDENT".equals(identer.getGruppe()))
                                .map(Person.Identer::getIdent)
                                .findFirst().orElse(null));

                        identdataDTO.setNavn(mapperFacade.map(person.getHentPerson().getNavn().stream()
                                .findFirst()
                                .orElse(new Person.Navn()), IdentdataDTO.NavnDTO.class));
                    }
                })
                .register();
    }
}
