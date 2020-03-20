package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class DelvisFravaer {
    @JsonProperty
    private Long id;
    @JsonProperty
    private LocalDate dato;
    @JsonProperty
    private double timer;
}
