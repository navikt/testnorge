package no.nav.dolly.bestilling.udistub;

import lombok.Data;
import no.nav.dolly.domain.resultset.udistub.model.PersonTo;

@Data
public class PersonControllerResponse {
    private PersonTo person;
    private String reason;
}
