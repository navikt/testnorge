package no.nav.dolly.bestilling.udistub.domain;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UdiPersonResponse {
    private UdiPerson person;
    private String reason;
    private HttpStatus status;
}
