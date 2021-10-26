package no.nav.dolly.bestilling.pdlforvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlVegadresse extends PdlOpplysning {

    private String adressekode;
    private String adressenavn;
    private String tilleggsnavn;
    private String bruksenhetsnummer;
    private String husbokstav;
    private String husnummer;
    private String kommunenummer;
    private String postnummer;
}