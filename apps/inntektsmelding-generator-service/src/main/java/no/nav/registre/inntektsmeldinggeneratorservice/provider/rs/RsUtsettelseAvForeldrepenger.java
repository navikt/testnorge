package no.nav.registre.inntektsmeldinggeneratorservice.provider.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsUtsettelseAvForeldrepenger {

    @JsonProperty
    private RsPeriode periode;
    @JsonProperty
    private String aarsakTilUtsettelse;

    public Optional<RsPeriode> getPeriode() {
        return Optional.ofNullable(periode);
    }

    public Optional<String> getAarsakTilUtsettelse() {
        return Optional.ofNullable(aarsakTilUtsettelse);
    }
}
