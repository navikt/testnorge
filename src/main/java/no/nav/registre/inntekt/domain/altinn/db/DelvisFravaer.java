package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@NoArgsConstructor(force = true)
public class DelvisFravaer {
    @JsonProperty
    Long id;
    @JsonProperty
    LocalDate dato;
    @JsonProperty
    double timer;
}
