package no.nav.dolly.bestilling.tagshendelseslager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HendelselagerResponse {

    private HttpStatus status;
    private String body;
    private String feilmelding;
}
