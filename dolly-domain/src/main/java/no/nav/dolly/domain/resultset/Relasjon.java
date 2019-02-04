package no.nav.dolly.domain.resultset;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Relasjon {

    private Long id;
    private Person person;
    private Person personRelasjonMed;
    private String relasjonTypeNavn;
}