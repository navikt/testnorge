package no.nav.dolly.bestilling.personservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonServiceResponse {

    private String ident;
    private Boolean exists;
    private HttpStatus status;
    private String feilmelding;
}
