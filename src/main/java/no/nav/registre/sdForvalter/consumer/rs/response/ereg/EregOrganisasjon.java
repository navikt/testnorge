package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;

@Slf4j
@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EregOrganisasjon {
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
    @JsonProperty
    private final List<EregOrganisasjon> inngaarIJuridiskEnheter = new ArrayList<>();

    public Organisasjon toOrganisasjon() {
        return Organisasjon
                .builder()
                .navn(navn.getNavnelinje1())
                .orgnummer(organisasjonsnummer)
                .juridiskEnhet(!inngaarIJuridiskEnheter.isEmpty()
                        ? inngaarIJuridiskEnheter.get(0).getOrganisasjonsnummer()
                        : null
                )
                .enhetType(type.equals("JuridiskEnhet")
                        ? juridiskEnhetDetaljer.getEnhetstype()
                        : virksomhetDetaljer.getEnhetstype()
                ).build();
    }

}
