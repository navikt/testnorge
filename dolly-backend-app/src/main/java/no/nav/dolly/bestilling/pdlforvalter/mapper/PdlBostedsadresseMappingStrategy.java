package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getCoadresse;
import static no.nav.dolly.bestilling.pdlforvalter.mapper.PdlAdresseMappingStrategy.getDato;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdresse.Vegadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlBostedadresse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlMatrikkeladresse;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlBostedsadresseMappingStrategy implements MappingStrategy {

    private static final String CO_NAME = "C/O";

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlBostedadresse.class)
                .customize(new CustomMapper<Person, PdlBostedadresse>() {
                    @Override
                    public void mapAtoB(Person person, PdlBostedadresse bostedadresse, MappingContext context) {

                        bostedadresse.setKilde(CONSUMER);

                        if (!person.getBoadresse().isEmpty()) {
                            bostedadresse.setFlyttedato(getDato(person.getBoadresse().get(0).getFlyttedato()));

                            if (person.isUtenFastBopel()) {
                                bostedadresse.setUkjentBosted(PdlBostedadresse.UkjentBosted.builder()
                                        .bostedskommune(person.getBoadresse().get(0).getKommunenr())
                                        .build());
                            } else {
                                if ("GATE".equals(person.getBoadresse().get(0).getAdressetype())) {
                                    bostedadresse.setVegadresse(mapperFacade.map(
                                            person.getBoadresse().get(0), Vegadresse.class));
                                } else {
                                    bostedadresse.setMatrikkeladresse(mapperFacade.map(
                                            person.getBoadresse().get(0), PdlMatrikkeladresse.class));
                                }
                                bostedadresse.setCoAdressenavn(getCoadresse(person.getBoadresse().get(0)));
                            }
                        }
                    }
                })
                .register();
    }
}
