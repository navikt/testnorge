package no.nav.testnav.altinn3tilgangservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganisasjonResponse {

    private String navn;
    private String organisasjonsnummer;
    private String organisasjonsform;
    private String miljoe;
    private String url;

    private String feilmelding;
}
