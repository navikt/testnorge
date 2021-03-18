package no.nav.registre.inntektsmeldinggeneratorservice.provider.rs;

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
public class RsPeriode {

    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private LocalDate tom;

    public Optional<LocalDate> getFom() {
        return Optional.ofNullable(fom);
    }

    public Optional<LocalDate> getTom() {
        return Optional.ofNullable(tom);
    }
}
