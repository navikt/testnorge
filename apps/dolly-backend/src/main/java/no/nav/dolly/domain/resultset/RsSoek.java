package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsSoek {

    private Long id;
    private JsonNode soekVerdi;
    private String opprettet;
    private RsSoekType soekType;
    private String feilmelding;

    public enum RsSoekType {
        DOLLY,
        TENOR
    }
}
