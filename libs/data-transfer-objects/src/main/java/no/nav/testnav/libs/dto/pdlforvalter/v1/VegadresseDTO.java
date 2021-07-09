package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VegadresseDTO implements Serializable {

    private String adressekode;
    private String adressenavn;
    private String adressetilleggsnavn;
    private String bruksenhetsnummer;
    private String husbokstav;
    private String husnummer;
    private String kommunenummer;
    private String postnummer;
}