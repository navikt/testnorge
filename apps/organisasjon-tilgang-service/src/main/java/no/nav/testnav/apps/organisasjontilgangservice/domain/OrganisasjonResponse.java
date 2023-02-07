package no.nav.testnav.apps.organisasjontilgangservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisasjonResponse {

    private String navn;
    private String organisasjonsnummer;
    private String organisasjonsform;
    private LocalDateTime gyldigTil;
    private String miljoe;
}
