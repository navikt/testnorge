package no.nav.testnav.identpool.dto;

import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;

import java.time.LocalDate;

public record ValideringResponseDTO(

        String ident,
        Identtype identtype,
        Boolean erTestnorgeIdent,
        Boolean erSyntetisk,
        Boolean erGyldig,
        Boolean erPersonnummer2032,
        LocalDate foedselsdato,
        Kjoenn kjoenn,
        String feilmelding,
        String kommentar) {
}