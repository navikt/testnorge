package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatusCode;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdResponseDTO {

    private HttpStatusCode statusCode;
    private String payload;
    private String feilmelding;
}
