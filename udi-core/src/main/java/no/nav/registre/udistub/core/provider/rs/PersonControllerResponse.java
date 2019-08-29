package no.nav.registre.udistub.core.provider.rs;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.udistub.core.service.to.PersonTo;

@Data
@NoArgsConstructor
public class PersonControllerResponse {
    private PersonTo person;
    private String exceptionReason;

    public PersonControllerResponse(PersonTo person) {
        this.person = person;
    }

    public PersonControllerResponse(String exceptionReason) {
        this.exceptionReason = exceptionReason;
    }
}
