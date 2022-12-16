package no.nav.dolly.bestilling.krrstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalKontaktdataResponse {

    private HttpStatus status;
    private String melding;
}
