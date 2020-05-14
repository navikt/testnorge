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
    @JsonProperty
    private String juridiskEnhet;
    @JsonProperty
    private Adresse postadresse;
    @JsonProperty
    private Adresse forretningsadresser;
    @JsonProperty
    private String redigertnavn;

    public Organisasjon(Ereg ereg) {
        orgnummer = ereg.getOrgnr();
        enhetType = ereg.getEnhetstype();
        navn = ereg.getNavn() != null ? ereg.getNavn().toUpperCase() : null;
        redigertnavn = ereg.getNavn() != null ? ereg.getNavn().toUpperCase() : null;
        juridiskEnhet = ereg.getJuridiskEnhet();
        postadresse = ereg.getPostadresse() != null ? new Adresse(ereg.getPostadresse()) : null;
        forretningsadresser = ereg.getForretningsAdresse() != null ? new Adresse(ereg.getForretningsAdresse()) : null;
    }
}
