package no.nav.registre.udistub.core.provider.rs;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.udistub.core.service.to.PersonTo;

import java.util.Map;

@Data
@NoArgsConstructor
public class PersonControllerResponse {
    private PersonTo person;
    private Map<String, Object> reason;

    public PersonControllerResponse(PersonTo person) {
        this.person = person;
    }

    public PersonControllerResponse(Map<String, Object> reason) {
        this.reason = reason;
    }
}
