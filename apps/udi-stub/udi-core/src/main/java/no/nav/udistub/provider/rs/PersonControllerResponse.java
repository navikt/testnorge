package no.nav.udistub.provider.rs;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.udistub.service.dto.UdiPerson;

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
