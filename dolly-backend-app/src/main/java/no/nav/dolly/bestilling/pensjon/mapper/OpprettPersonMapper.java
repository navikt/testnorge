package no.nav.dolly.bestilling.pensjon.mapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPersonRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class OpprettPersonMapper implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Person.class, OpprettPersonRequest.class)
                .customize(new CustomMapper<Person, OpprettPersonRequest>() {
                    @Override
                    public void mapAtoB(Person person, OpprettPersonRequest opprettPersonRequest, MappingContext context) {

                        opprettPersonRequest.setFodselsDato(mapperFacade.map(person.getFoedselsdato(), LocalDate.class));
                        opprettPersonRequest.setDodsDato(mapperFacade.map(person.getDoedsdato(), LocalDate.class));
                        opprettPersonRequest.setUtvandringsDato(mapperFacade.map(person.getUtvandretTilLandFlyttedato(), LocalDate.class));

                        if (!person.getBoadresse().isEmpty()) {
                            opprettPersonRequest.setBostedsland("NOR");
                        } else if (!person.getPostadresse().isEmpty()) {
                            opprettPersonRequest.setBostedsland(person.getPostadresse().get(0).getPostLand());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
