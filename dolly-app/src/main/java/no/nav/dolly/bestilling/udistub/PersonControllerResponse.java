package no.nav.dolly.bestilling.udistub;

import lombok.Builder;
import lombok.Data;
import no.nav.dolly.domain.resultset.udistub.model.PersonTo;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PersonControllerResponse {
    private PersonTo person;
    private String reason;
    private HttpStatus status;
}
