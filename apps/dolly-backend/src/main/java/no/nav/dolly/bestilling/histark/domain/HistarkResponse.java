package no.nav.dolly.bestilling.histark.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistarkResponse {

    private String id;
    private HttpStatus status;
    private String feilmelding;
}
