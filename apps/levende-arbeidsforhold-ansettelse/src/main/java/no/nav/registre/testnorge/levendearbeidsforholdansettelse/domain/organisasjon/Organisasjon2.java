package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.organisasjon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
//import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.organisasjon.dto.OrganisasjonDTO;

@Value
@Builder
@AllArgsConstructor
public class Organisasjon2 {
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
/*
    public Organisasjon2(Ereg ereg) {
        orgnummer = ereg.getOrgnr();
        enhetType = ereg.getEnhetstype();
        navn = ereg.getNavn() != null ? ereg.getNavn().toUpperCase() : null;
        redigertnavn = ereg.getRedigertNavn() != null ? ereg.getRedigertNavn().toUpperCase() : null;
        juridiskEnhet = ereg.getJuridiskEnhet();
        postadresse = ereg.getPostadresse() != null ? new Adresse(ereg.getPostadresse()) : null;
        forretningsadresser = ereg.getForretningsAdresse() != null ? new Adresse(ereg.getForretningsAdresse()) : null;
    }
*/
    public Organisasjon2(OrganisasjonDTO dto) {
        orgnummer = dto.getOrganisasjonsnummer();
        enhetType = dto.getType();
        navn = dto.getNavn().getNavnelinje1();
        juridiskEnhet = dto.getDetaljer().getEnhetstype();
        postadresse = dto.getOrganisasjonDetaljer().getPostadresser() != null ? new Adresse(dto.getOrganisasjonDetaljer().getPostadresser().getFirst()) : null;
        forretningsadresser = dto.getOrganisasjonDetaljer().getForretningsadresser() != null ? new Adresse(dto.getOrganisasjonDetaljer().getForretningsadresser().getFirst()) : null;
        redigertnavn = dto.getNavn().getRedigertnavn();
    }
}
