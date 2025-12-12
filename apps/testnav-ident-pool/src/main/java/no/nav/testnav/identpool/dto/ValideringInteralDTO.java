package no.nav.testnav.identpool.dto;

import no.nav.testnav.identpool.domain.Identtype;

public record ValideringInteralDTO(

        String valideringResultat,
        Boolean erGyldig,
        Boolean erSyntetisk,
        Boolean erStriktFoedselsnummer64,
        Boolean erTestnorgeIdent,
        Boolean erId2032Ident,
        Identtype identtype
) {
}
