package no.nav.dolly.bestilling.pensjon.mapper;

import java.time.LocalDate;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjon.domain.OpprettPerson;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class OpprettPersonMapper implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Person.class, OpprettPerson.class)
                .customize(new CustomMapper<Person, OpprettPerson>() {
                    @Override
                    public void mapAtoB(Person person, OpprettPerson opprettPerson, MappingContext context) {

                        opprettPerson.setFodselsDato(mapperFacade.map(person.getFoedselsdato(), LocalDate.class));
                        opprettPerson.setDodsDato(mapperFacade.map(person.getDoedsdato(), LocalDate.class));
                        opprettPerson.setUtvandringsDato(mapperFacade.map(person.getUtvandretTilLandFlyttedato(), LocalDate.class));

                        if (!person.getBoadresse().isEmpty()) {
                            opprettPerson.setBostedsland("NOR");
                        } else if (!person.getPostadresse().isEmpty()) {
                            opprettPerson.setBostedsland(person.getPostadresse().get(0).getPostLand());
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
