package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FoedselDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

@UtilityClass
public class FoedselsdatoUtility {

    private static final int MYNDIGHETSALDER = 18;

    public LocalDateTime getFoedselsdato(PersonDTO person) {

        return person.getFoedsel().stream()
                .map(FoedselDTO::getFoedselsdato)
                .findFirst()
                .orElse(DatoFraIdentUtility.getDato(person.getIdent()).atStartOfDay());
    }

    public boolean isMyndig(PersonDTO person) {

        var foedselsdato = getFoedselsdato(person);

        return foedselsdato.plusYears(MYNDIGHETSALDER).isBefore(LocalDateTime.now()) ||
                foedselsdato.toLocalDate().plusYears(MYNDIGHETSALDER).isEqual(LocalDate.now());
    }

    public LocalDateTime getMyndighetsdato(PersonDTO person) {

        return getFoedselsdato(person).plusYears(MYNDIGHETSALDER);
    }
}
