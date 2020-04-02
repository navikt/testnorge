package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class DelvisFravaer {
    @JsonProperty
    private Long id;
    @JsonProperty
    private LocalDate dato;
    @JsonProperty
    private double timer;
}
