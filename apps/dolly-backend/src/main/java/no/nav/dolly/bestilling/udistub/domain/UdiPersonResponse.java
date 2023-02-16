package no.nav.dolly.bestilling.udistub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdiPersonResponse {

    public enum InnsendingType {NEW, UPDATE}

    private UdiPerson person;
    private String reason;
    private HttpStatus status;

    private InnsendingType type;
}
