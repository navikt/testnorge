package no.nav.dolly.bestilling.inntektstub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckImportResponse {

    private HttpStatus status;
    private String message;
}
