package no.nav.registre.populasjoner.kafka.person;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IdentDetaljDto implements Dto {

    String ident;
    boolean historisk;
    IdentGruppeDto gruppe;

    public static IdentGruppeDto mapIdentGruppe(String identGruppe) {
        switch (identGruppe.toUpperCase()) {
        case "AKTOR_ID":
            return IdentGruppeDto.AKTORID;
        case "FOLKEREGISTERIDENT":
            return IdentGruppeDto.FOLKEREGISTERIDENT;
        case "N_PID":
            return IdentGruppeDto.NPID;
        default:
            throw new RuntimeException("Unable to map '" + identGruppe + " to IdentGruppe");
        }
    }
}

