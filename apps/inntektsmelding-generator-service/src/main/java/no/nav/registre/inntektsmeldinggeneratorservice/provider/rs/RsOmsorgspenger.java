package no.nav.registre.inntektsmeldinggeneratorservice.provider.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsOmsorgspenger {

    @JsonProperty
    private Boolean harUtbetaltPliktigeDager;
    @JsonProperty
    private List<RsPeriode> fravaersPerioder;
    @JsonProperty
    private List<RsDelvisFravaer> delvisFravaersListe;

    public Optional<Boolean> getHarUtbetaltPliktigeDager() {
        return Optional.ofNullable(harUtbetaltPliktigeDager);
    }

    public Optional<List<RsPeriode>> getFravaersPerioder() {
        return Optional.ofNullable(fravaersPerioder);
    }

    public Optional<List<RsDelvisFravaer>> getDelvisFravaersListe() {
        return Optional.ofNullable(delvisFravaersListe);
    }
}
