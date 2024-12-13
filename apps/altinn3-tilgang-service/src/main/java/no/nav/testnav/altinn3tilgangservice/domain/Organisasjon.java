package no.nav.testnav.altinn3tilgangservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organisasjon {

    private String navn;
    private String organisasjonsnummer;
    private String organisasjonsform;
    private String url;

    private String feilmelding;
    private HttpStatus status;
}
