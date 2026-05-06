package no.nav.dolly.bestilling.kelvinaap.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AapOpprettResponse {

    private String saksnummer;

    private HttpStatus status;
    private String error;
}
