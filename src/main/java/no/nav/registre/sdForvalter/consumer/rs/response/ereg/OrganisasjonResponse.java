package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;

@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonResponse {
    @JsonProperty(required = true)
    private final String organisasjonsnummer;
    @JsonProperty
    private final VirksomhetDetaljer virksomhetDetaljer;
    @JsonProperty
    private final JuridiskEnhetDetaljer juridiskEnhetDetaljer;
    @JsonProperty(required = true)
    private final Navn navn;
    @JsonProperty(required = true)
    private final String type;


    public Organisasjon toOrganisasjon() {
        return Organisasjon
                .builder()
                .navn(navn.getNavnelinje1())
                .orgnummer(organisasjonsnummer)
                .enhetType(type.equals("JuridiskEnhet")
                        ? juridiskEnhetDetaljer.getEnhetstype()
                        : virksomhetDetaljer.getEnhetstype()
                ).build();
    }

}
