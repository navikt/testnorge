package no.nav.dolly.bestilling.udistub;

import lombok.Builder;
import lombok.Data;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class PersonControllerResponse {
    private UdiPerson person;
    private String reason;
    private HttpStatus status;
}
