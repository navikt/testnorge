package no.nav.dolly.bestilling.udistub;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.udistub.model.UdiPerson;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdiPersonControllerResponse {
    private UdiPerson person;
    private String reason;
    private HttpStatus status;
}
