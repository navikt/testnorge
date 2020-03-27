package no.nav.registre.sdForvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import no.nav.registre.sdForvalter.consumer.rs.response.ereg.OrganisasjonResponse;
import no.nav.registre.sdForvalter.domain.Ereg;

@Value
public class Organisasjon {
    @JsonProperty(required = true)
    private String orgnummer;
    @JsonProperty
    private String enhetType;
    @JsonProperty
    private String navn;

    public Organisasjon(OrganisasjonResponse organisasjon) {
        orgnummer = organisasjon.getOrganisasjonsnummer();
        enhetType = organisasjon.getVirksomhetDetaljer().getEnhetstype();
        navn = organisasjon.getNavn().getNavnelinje1();
    }

    public Organisasjon(Ereg ereg){
        orgnummer = ereg.getOrgnr();
        enhetType = ereg.getEnhetstype();
        navn = ereg.getNavn();
    }
}
