package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.mapper.MappingStrategy;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.INNVANDRET;
import static no.nav.dolly.domain.resultset.tpsf.InnvandretUtvandret.InnUtvandret.UTVANDRET;

@Component
public class OpprettPersonMapper implements MappingStrategy {

    private static final DateTimeFormatter DATE_FMT = ofPattern("yyyy-MM-dd");

    private static String convertDate(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.format(DATE_FMT) : null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(Person.class, OpprettPersonRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(Person person, OpprettPersonRequest opprettPersonRequest, MappingContext context) {

                        opprettPersonRequest.setFnr(person.getIdent());
                        opprettPersonRequest.setFodselsDato(convertDate(person.getFoedselsdato()));
                        opprettPersonRequest.setDodsDato(convertDate(person.getDoedsdato()));

                        if (person.getInnvandretUtvandret().isEmpty() ||
                                person.getInnvandretUtvandret().get(0).getInnutvandret() == INNVANDRET) {

                            opprettPersonRequest.setBostedsland("NOR");

                        } else if (!person.getInnvandretUtvandret().isEmpty() &&
                                person.getInnvandretUtvandret().get(0).getInnutvandret() == UTVANDRET) {

                            opprettPersonRequest.setBostedsland(person.getInnvandretUtvandret().get(0).getLandkode());
                            opprettPersonRequest.setUtvandringsDato(
                                    convertDate(person.getInnvandretUtvandret().get(0).getFlyttedato()));
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
