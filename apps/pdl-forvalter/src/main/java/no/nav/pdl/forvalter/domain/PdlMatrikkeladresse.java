package no.nav.pdl.forvalter.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PdlMatrikkeladresse implements Serializable {

    private String adressetilleggsnavn;
    private String bruksenhetsnummer;
    private PdlAdresse.Bruksenhetstype bruksenhetstype;
    private Integer bruksnummer;
    private Integer festenummer;
    private Integer gaardsnummer;
    private String kommunenummer;
    private String postnummer;
    private Integer undernummer;
}
