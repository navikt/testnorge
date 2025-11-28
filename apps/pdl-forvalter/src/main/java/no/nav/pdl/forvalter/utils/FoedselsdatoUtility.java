package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselsdatoDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

@UtilityClass
public class FoedselsdatoUtility {

    private static final int MYNDIGHETSALDER = 18;

    public LocalDateTime getFoedselsdato(PersonDTO person) {

        return person.getFoedselsdato().stream()
                .filter(foedsel -> nonNull(foedsel.getFoedselsdato()) || nonNull(foedsel.getFoedselsaar()))
                .map(foedsel-> getFoedselsdato(foedsel, person.getIdent()))
                .findFirst()
                .orElse(person.getFoedsel().stream()
                        .filter(foedsel -> nonNull(foedsel.getFoedselsdato()) || nonNull(foedsel.getFoedselsaar()))
                        .map(foedsel-> getFoedselsdato(foedsel, person.getIdent()))
                        .findFirst()
                        .orElse(DatoFraIdentUtility.getDato(person.getIdent()).atStartOfDay()));
    }

    private static LocalDateTime getFoedselsdato(FoedselsdatoDTO foedsel, String ident) {

        return nonNull(foedsel.getFoedselsdato()) ? foedsel.getFoedselsdato() :
                LocalDate.of(foedsel.getFoedselsaar(),
                        DatoFraIdentUtility.getDato(ident).getMonthValue(),
                        DatoFraIdentUtility.getDato(ident).getDayOfMonth()).atStartOfDay();
    }

    public static boolean isMyndig(PersonDTO person) {

        var foedselsdato = getFoedselsdato(person);

        return foedselsdato.plusYears(MYNDIGHETSALDER).isBefore(LocalDateTime.now()) ||
                foedselsdato.toLocalDate().plusYears(MYNDIGHETSALDER).isEqual(LocalDate.now());
    }

    public static LocalDateTime getMyndighetsdato(PersonDTO person) {

        return getFoedselsdato(person).plusYears(MYNDIGHETSALDER);
    }
}
