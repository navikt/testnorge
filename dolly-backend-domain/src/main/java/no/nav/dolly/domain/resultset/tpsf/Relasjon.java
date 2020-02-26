package no.nav.dolly.domain.resultset.tpsf;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relasjon {

    private Long id;
    private Person person;
    private Person personRelasjonMed;
    private String relasjonTypeNavn;

    public boolean isPartner() {
        return "PARTNER".equals(getRelasjonTypeNavn());
    }

    public boolean isBarn() {
        return "FOEDSEL".equals(getRelasjonTypeNavn()) || "BARN".equals(getRelasjonTypeNavn());
    }
}