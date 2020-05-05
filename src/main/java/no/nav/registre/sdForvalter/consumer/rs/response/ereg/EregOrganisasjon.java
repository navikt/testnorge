package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sdForvalter.domain.status.ereg.Adresse;
import no.nav.registre.sdForvalter.domain.status.ereg.Organisasjon;

@Slf4j
@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EregOrganisasjon {
    @JsonProperty(required = true)
    private final String organisasjonsnummer;
    @JsonProperty(required = true)
    @JsonAlias({"virksomhetDetaljer", "juridiskEnhetDetaljer", "organisasjonsleddDetaljer"})
    private final Detaljer detaljer;
    @JsonProperty(required = true)
    private final Navn navn;
    @JsonProperty(required = true)
    private final String type;
    @JsonProperty
    @JsonAlias({"inngaarIJuridiskEnheter"})
    private final List<EregOrganisasjon> parents = new ArrayList<>();
    @JsonProperty
    private final OrganisasjonDetaljer organisasjonDetaljer;
    @JsonProperty
    @JsonAlias({"driverVirksomheter"})
    private final List<EregOrganisasjon> children = new ArrayList<>();

    public Organisasjon toOrganisasjon() {
        Organisasjon.OrganisasjonBuilder builder = Organisasjon
                .builder()
                .navn(navn.getNavnelinje1())
                .orgnummer(organisasjonsnummer)
                .juridiskEnhet(!parents.isEmpty()
                        ? parents.get(0).getOrganisasjonsnummer()
                        : null
                )
                .enhetType(detaljer.getEnhetstype());

        if (organisasjonDetaljer != null && organisasjonDetaljer.getPostadresser() != null && !organisasjonDetaljer.getPostadresser().isEmpty()) {
            EregAdresse postadresse = organisasjonDetaljer.getPostadresser().get(0);
            builder.postadresse(Adresse.builder()
                    .kommunenummer(postadresse.getKommunenummer())
                    .build());
        }

        if (organisasjonDetaljer != null && organisasjonDetaljer.getForretningsadresser() != null && !organisasjonDetaljer.getForretningsadresser().isEmpty()) {
            EregAdresse forretningsadresse = organisasjonDetaljer.getForretningsadresser().get(0);
            builder.forretningsadresser(
                    Adresse.builder()
                            .kommunenummer(forretningsadresse.getKommunenummer())
                            .build()
            );
        }

        return builder.build();
    }
}
