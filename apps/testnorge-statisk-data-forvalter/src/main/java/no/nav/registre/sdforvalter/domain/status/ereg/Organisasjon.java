package no.nav.registre.sdforvalter.domain.status.ereg;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

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
        redigertnavn = ereg.getRedigertNavn() != null ? ereg.getRedigertNavn().toUpperCase() : null;
        juridiskEnhet = ereg.getJuridiskEnhet();
        postadresse = ereg.getPostadresse() != null ? new Adresse(ereg.getPostadresse()) : null;
        forretningsadresser = ereg.getForretningsAdresse() != null ? new Adresse(ereg.getForretningsAdresse()) : null;
    }

    public Organisasjon(OrganisasjonDTO dto) {
        orgnummer = dto.getOrgnummer();
        enhetType = dto.getEnhetType();
        navn = dto.getNavn();
        juridiskEnhet = dto.getJuridiskEnhet();
        postadresse = dto.getPostadresse() != null ? new Adresse(dto.getPostadresse()) : null;
        forretningsadresser = dto.getForretningsadresser() != null ? new Adresse(dto.getForretningsadresser()) : null;
        redigertnavn = dto.getRedigertnavn();
    }
}
