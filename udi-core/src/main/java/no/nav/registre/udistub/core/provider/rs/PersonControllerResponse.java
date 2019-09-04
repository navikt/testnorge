package no.nav.registre.udistub.core.provider.rs;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.udistub.core.service.to.UdiPerson;

import java.util.Map;

@Data
@NoArgsConstructor
public class PersonControllerResponse {
    private UdiPerson person;
    private Map<String, Object> reason;

    public PersonControllerResponse(UdiPerson person) {
        this.person = person;
    }

    public PersonControllerResponse(Map<String, Object> reason) {
        this.reason = reason;
    }
}
