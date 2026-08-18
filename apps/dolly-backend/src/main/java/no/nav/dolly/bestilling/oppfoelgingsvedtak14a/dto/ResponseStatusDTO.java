package no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStatusDTO {

    private HttpStatus status;
    private String reason;
}
