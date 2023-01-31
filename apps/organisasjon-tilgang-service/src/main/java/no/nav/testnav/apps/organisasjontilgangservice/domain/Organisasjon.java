package no.nav.testnav.apps.organisasjontilgangservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.OrganisasjonDTO;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.RightDTO;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organisasjon {

    private String navn;
    private String organisasjonsnummer;
    private String organisasjonsform;
    private LocalDateTime gyldigTil;

    public Organisasjon(OrganisasjonDTO dto, RightDTO rightDTO) {
        this.navn = dto.Name();
        this.organisasjonsnummer = dto.OrganizationNumber();
        this.organisasjonsform = dto.Type();
        this.gyldigTil = rightDTO.validTo();
    }
}
