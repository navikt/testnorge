package no.nav.registre.skd.provider.rs.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FastMeldingRequest {

    private String foedselsdato;
    private String personnummer;
    private String fornavn;
    private String etternavn;
    private String adresse;
    private String postnr;
    private String by;
}
