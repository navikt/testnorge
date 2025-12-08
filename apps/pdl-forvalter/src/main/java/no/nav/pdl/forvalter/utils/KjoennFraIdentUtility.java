package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;

import static java.lang.Integer.parseInt;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.KVINNE;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.KjoennDTO.Kjoenn.MANN;

@UtilityClass
public class KjoennFraIdentUtility {

    public KjoennDTO.Kjoenn getKjoenn(String ident) {

        if (PersonDTO.isId2032(ident)) {
            return KjoennUtility.getKjoenn();
        }
        var kjoennNummer = parseInt(ident.substring(8, 9));
        return kjoennNummer % 2 == 0 ? KVINNE : MANN;
    }

    public KjoennDTO.Kjoenn getKjoenn(PersonDTO person) {

        return person.getKjoenn().stream()
                .findFirst()
                .orElse(new KjoennDTO())
                .getKjoenn();
    }
}