package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenorResponse {

    private HttpStatus status;
    private JsonNode data;
    private String query;
    private String error;
}