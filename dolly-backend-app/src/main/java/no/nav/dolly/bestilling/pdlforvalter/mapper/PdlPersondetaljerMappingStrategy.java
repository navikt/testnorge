package no.nav.dolly.bestilling.pdlforvalter.mapper;

import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.FORTROLIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse.AdresseBeskyttelse.UGRADERT;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn.Kjoenn.KVINNE;
import static no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn.Kjoenn.MANN;

import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pdlforvalter.PdlForvalterClient;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlAdressebeskyttelse;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlDoedsfall;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlFoedsel;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlKjoenn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class PdlPersondetaljerMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(Person.class, PdlFoedsel.class)
                .customize(new CustomMapper<Person, PdlFoedsel>() {
                    @Override
                    public void mapAtoB(Person person, PdlFoedsel pdlFoedsel, MappingContext context) {

                        pdlFoedsel.setKilde(PdlForvalterClient.KILDE);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(Person.class, PdlNavn.class)
                .customize(new CustomMapper<Person, PdlNavn>() {
                    @Override
                    public void mapAtoB(Person person, PdlNavn pdlNavn, MappingContext context) {

                        pdlNavn.setKilde(PdlForvalterClient.KILDE);
                    }
                })
                .byDefault()
                .register();


        factory.classMap(Person.class, PdlKjoenn.class)
                .customize(new CustomMapper<Person, PdlKjoenn>() {
                    @Override
                    public void mapAtoB(Person person, PdlKjoenn pdlKjoenn, MappingContext context) {

                        pdlKjoenn.setKjoenn("M".equals(person.getKjonn()) ? MANN : KVINNE);
                        pdlKjoenn.setKilde(PdlForvalterClient.KILDE);
                    }
                })
                .register();

        factory.classMap(Person.class, PdlAdressebeskyttelse.class)
                .customize(new CustomMapper<Person, PdlAdressebeskyttelse>() {
                    @Override
                    public void mapAtoB(Person person, PdlAdressebeskyttelse adressebeskyttelse, MappingContext context) {

                        if ("SPSF".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(STRENGT_FORTROLIG);

                        } else if ("SPFO".equals(person.getSpesreg())) {
                            adressebeskyttelse.setGradering(FORTROLIG);

                        } else {
                            adressebeskyttelse.setGradering(UGRADERT);
                        }

                        adressebeskyttelse.setKilde(PdlForvalterClient.KILDE);
                    }
                })
                .register();


        factory.classMap(Person.class, PdlDoedsfall.class)
                .customize(new CustomMapper<Person, PdlDoedsfall>() {
                    @Override
                    public void mapAtoB(Person person, PdlDoedsfall pdlDoedsfall, MappingContext context) {

                        pdlDoedsfall.setKilde(PdlForvalterClient.KILDE);
                    }
                })
                .byDefault()
                .register();
    }
}