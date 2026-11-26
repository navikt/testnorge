package no.nav.testnav.libs.dto.identpool.v2;

import no.nav.testnav.libs.dto.identpool.v1.Identtype;
import no.nav.testnav.libs.dto.identpool.v1.Kjoenn;

import java.time.LocalDate;

public record ValideringResponseDTO(
    
            String ident,
            Identtype identtype,
            Boolean erTestnorgeIdent,
            Boolean erSyntetisk,
            Boolean erGyldig,
            Boolean erIProd,
            Boolean erPersonnummer2032,
            LocalDate foedselsdato,
            Kjoenn kjoenn,
            String feilmelding,
            String kommentar) {
    }