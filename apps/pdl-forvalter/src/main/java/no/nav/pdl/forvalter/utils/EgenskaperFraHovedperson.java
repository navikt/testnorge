package no.nav.pdl.forvalter.utils;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonRequestDTO;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.pdl.forvalter.utils.SyntetiskFraIdentUtility.isSyntetisk;

@UtilityClass
public class EgenskaperFraHovedperson {

    /**
     * Kopierer identtype, syntetisk og id2032 fra hovedperson til request
     */
    public static void kopierData(PersonDTO hovedperson, PersonRequestDTO request) {

        if (isNull(request.getIdenttype())) {
            request.setIdenttype(nonNull(hovedperson.getIdenttype()) ? hovedperson.getIdenttype() :
                    IdenttypeUtility.getIdenttype(hovedperson.getIdent()));
        }
        if (isNull(request.getSyntetisk())) {
            request.setSyntetisk(isSyntetisk(hovedperson.getIdent()));
        }
        if (isNull(request.getId2032())) {
            request.setId2032(hovedperson.getId2032());
        }
    }

    public static void kopierData(String hovedperson, PersonRequestDTO request) {

        kopierData(PersonDTO.builder().ident(hovedperson).build(), request);
    }
}
