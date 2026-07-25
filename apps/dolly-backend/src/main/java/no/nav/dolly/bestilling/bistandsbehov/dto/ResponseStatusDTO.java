package no.nav.dolly.bestilling.bistandsbehov.dto;

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
