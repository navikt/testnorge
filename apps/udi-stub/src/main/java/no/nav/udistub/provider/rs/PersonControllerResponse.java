package no.nav.udistub.provider.rs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.udistub.service.dto.UdiPerson;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonControllerResponse {

    private UdiPerson person;
    private Map<String, Object> reason;
}