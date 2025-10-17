package no.nav.dolly.util;

import lombok.experimental.UtilityClass;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@UtilityClass
public class FoedselsdatoUtility {

    private static final int MYNDIGHETSALDER = 18;

    public LocalDateTime getFoedselsdato(PdlPersonBolk.PersonBolk personBolk) {

        return personBolk.getPerson().getFoedselsdato().stream()
                .filter(foedsel -> nonNull(foedsel.getFoedselsdato()) || nonNull(foedsel.getFoedselsaar()))
                .map(foedsel-> getFoedselsdato(foedsel, personBolk.getIdent()))
                .findFirst()
                .orElse(DatoFraIdentUtil.getDato(personBolk.getIdent()).atStartOfDay());
    }

    private static LocalDateTime getFoedselsdato(PdlPerson.Foedselsdato foedsel, String ident) {

        return nonNull(foedsel.getFoedselsdato()) ? foedsel.getFoedselsdato().atStartOfDay() :
                LocalDate.of(foedsel.getFoedselsaar(),
                        DatoFraIdentUtil.getDato(ident).getMonthValue(),
                        DatoFraIdentUtil.getDato(ident).getDayOfMonth()).atStartOfDay();
    }

    public static boolean isMyndig(PdlPersonBolk.PersonBolk personBolk) {

        var foedselsdato = getFoedselsdato(personBolk);

        return foedselsdato.plusYears(MYNDIGHETSALDER).isBefore(LocalDateTime.now()) ||
                foedselsdato.toLocalDate().plusYears(MYNDIGHETSALDER).isEqual(LocalDate.now());
    }

    public static LocalDateTime getMyndighetsdato(PdlPersonBolk.PersonBolk personBolk) {

        return getFoedselsdato(personBolk).plusYears(MYNDIGHETSALDER);
    }
}
