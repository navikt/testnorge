package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.dolly.util.DatoFraIdentUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Objects.nonNull;

@Component
public class PensjonPersonMappingStrategy implements MappingStrategy {

    private static final DateTimeFormatter DATE_FMT = ofPattern("yyyy-MM-dd");

    private static String convertDato(LocalDate date) {

        return nonNull(date) ? date.format(DATE_FMT) : null;
    }

    private static String getFoedselsdato(LocalDate dateTime, String ident) {

        return convertDato(nonNull(dateTime) ? dateTime : DatoFraIdentUtil.getDato(ident));
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(PdlPersonBolk.PersonBolk.class, OpprettPersonRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PdlPersonBolk.PersonBolk person, OpprettPersonRequest opprettPersonRequest, MappingContext context) {

                        opprettPersonRequest.setFnr(person.getIdent());

                        opprettPersonRequest.setFodselsDato(getFoedselsdato(person.getPerson().getFoedsel().stream()
                                .map(PdlPerson.Foedsel::getFoedselsdato)
                                .findFirst().orElse(null), opprettPersonRequest.getFnr()));

                        opprettPersonRequest.setDodsDato(convertDato(person.getPerson().getDoedsfall().stream()
                                .map(PdlPerson.Doedsfall::getDoedsdato)
                                .findFirst().orElse(null)));

                        if (person.getPerson().getUtflyttingFraNorge().stream()
                                            .noneMatch(utflytting -> person.getPerson().getInnflyttingTilNorge().stream()
                                                            .anyMatch(innflytting -> innflytting.getInnflyttingsdato()
                                                                    .isAfter(utflytting.getUtflyttingsdato())))) {

                            opprettPersonRequest.setBostedsland(person.getPerson().getUtflyttingFraNorge().stream()
                                    .map(PdlPerson.UtflyttingFraNorge::getTilflyttingsland)
                                    .findFirst().orElse("NOR"));

                            opprettPersonRequest.setUtvandringsDato(
                                    convertDato(person.getPerson().getUtflyttingFraNorge().stream()
                                            .map(PdlPerson.UtflyttingFraNorge::getUtflyttingsdato)
                                            .filter(Objects::nonNull)
                                            .map(LocalDateTime::toLocalDate)
                                            .findFirst().orElse(null)));
                        } else {

                            opprettPersonRequest.setBostedsland("NOR");
                        }
                    }
                })
                .byDefault()
                .register();
    }
}
