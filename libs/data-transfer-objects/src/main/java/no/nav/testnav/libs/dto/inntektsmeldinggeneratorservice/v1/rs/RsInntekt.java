package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
public class RsInntekt {

    @JsonProperty
    private Double beloep;
    @JsonProperty
    private String aarsakVedEndring;

    public Optional<Double> getBeloep() {
        return Optional.ofNullable(beloep);
    }

    public Optional<String> getAarsakVedEndring() {
        return Optional.ofNullable(aarsakVedEndring);
    }
}
