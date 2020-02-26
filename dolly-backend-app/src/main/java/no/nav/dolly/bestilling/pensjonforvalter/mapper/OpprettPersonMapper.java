package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;

@Component
public class OpprettPersonMapper implements MappingStrategy {

    private static final DateTimeFormatter DATE_FMT = ofPattern("yyyy-MM-dd");

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Person.class, OpprettPersonRequest.class)
                .customize(new CustomMapper<Person, OpprettPersonRequest>() {
                    @Override
                    public void mapAtoB(Person person, OpprettPersonRequest opprettPersonRequest, MappingContext context) {

                        opprettPersonRequest.setFodselsDato(convertDate(person.getFoedselsdato()));
                        opprettPersonRequest.setDodsDato(convertDate(person.getDoedsdato()));
                        opprettPersonRequest.setUtvandringsDato(convertDate(person.getUtvandretTilLandFlyttedato()));

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

    private static String convertDate(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.format(DATE_FMT) : null;
    }
}
