package no.nav.registre.testnorge.libs.dto.organisasjon.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisasjonDTO {
    private final String orgnummer;
    private final String enhetType;
    private final String navn;
    private final String juridiskEnhet;
    private final AdresseDTO postadresse;
    private final AdresseDTO forretningsadresser;
    private final String redigertnavn;
    private final List<String> driverVirksomheter;

    public OrganisasjonDTO(OrganisasjonDTO dto) {
        this.orgnummer = dto.getOrgnummer();
        this.enhetType = dto.getEnhetType();
        this.navn = dto.getNavn();
        this.juridiskEnhet = dto.getJuridiskEnhet();
        this.postadresse = dto.getPostadresse();
        this.forretningsadresser = dto.getForretningsadresser();
        this.redigertnavn = dto.getRedigertnavn();
        this.driverVirksomheter = dto.getDriverVirksomheter();
    }
}
