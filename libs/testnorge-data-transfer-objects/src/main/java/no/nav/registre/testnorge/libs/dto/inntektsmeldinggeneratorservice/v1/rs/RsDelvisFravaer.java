package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
