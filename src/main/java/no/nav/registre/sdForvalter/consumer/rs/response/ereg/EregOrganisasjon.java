package no.nav.registre.sdForvalter.consumer.rs.response.ereg;

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
    @JsonProperty
    private final List<EregAdresse> forretningsadresser;
    @JsonProperty
    private final List<EregAdresse> postadresser;

    public Organisasjon toOrganisasjon() {
        Organisasjon.OrganisasjonBuilder builder = Organisasjon
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
                );


        if(postadresser!= null && !postadresser.isEmpty()){
            EregAdresse postadresse = postadresser.get(0);
            builder.postadresse(Adresse.builder()
                    .kommunenummer(postadresse.getKommunenummer())
                    .build());
        }

        if(forretningsadresser!= null && !forretningsadresser.isEmpty()){
            EregAdresse forretningsadresse = forretningsadresser.get(0);
            builder.forretningsadresser(
                    Adresse.builder()
                            .kommunenummer(forretningsadresse.getKommunenummer())
                            .build()
            );
        }

        return builder.build();
    }
}
