
package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InaktiverResponse {

    private HttpStatus status;
    private String feilmelding;
    private String miljoe;
}