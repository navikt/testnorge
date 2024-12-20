package no.nav.testnav.apps.brukerservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.altinn3.v1.OrganisasjonDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Organisasjon {

    private String navn;
    private String organisasjonsnummer;
    private String organisasjonsform;

    public Organisasjon(OrganisasjonDTO dto) {
        this.navn = dto.getNavn();
        this.organisasjonsnummer = dto.getOrganisasjonsnummer();
        this.organisasjonsform = dto.getOrganisasjonsform();
    }
}
