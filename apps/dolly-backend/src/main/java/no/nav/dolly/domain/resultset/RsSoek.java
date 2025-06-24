package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsSoek {

    private Long id;
    private JsonNode soekVerdi;
    private LocalDateTime opprettet;
    private RsSoekType soekType;
    private String feilmelding;

    public enum RsSoekType {
        DOLLY,
        TENOR
    }
}
