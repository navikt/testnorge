package no.nav.registre.sdForvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import no.nav.registre.sdForvalter.domain.Ereg;

@Value
@Builder
@AllArgsConstructor
public class Organisasjon {
    @JsonProperty(required = true)
    private String orgnummer;
    @JsonProperty
    private String enhetType;
    @JsonProperty
    private String navn;

    public Organisasjon(Ereg ereg) {
        orgnummer = ereg.getOrgnr();
        enhetType = ereg.getEnhetstype();
        navn = ereg.getNavn();
    }
}
