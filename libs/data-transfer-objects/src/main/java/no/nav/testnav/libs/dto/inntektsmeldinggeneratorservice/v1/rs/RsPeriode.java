package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Optional;

@Data
@NoArgsConstructor
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
