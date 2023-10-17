package no.nav.dolly.bestilling.medl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedlPostResponse {

    private HttpStatus status;
    private String melding;
}
