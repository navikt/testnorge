package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
public class RsDelvisFravaer {

    @JsonProperty
    private LocalDate dato;
    @JsonProperty
    private Double timer;

    public Optional<LocalDate> getDato() {
        return Optional.ofNullable(dato);
    }

    public Optional<Double> getTimer() {
        return Optional.ofNullable(timer);
    }
}
