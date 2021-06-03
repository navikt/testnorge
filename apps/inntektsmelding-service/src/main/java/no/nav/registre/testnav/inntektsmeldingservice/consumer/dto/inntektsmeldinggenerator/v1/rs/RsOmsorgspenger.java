package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsOmsorgspenger {

    @JsonProperty
    private Boolean harUtbetaltPliktigeDager;

    @JsonProperty
    private List<RsPeriode> fravaersPerioder;

    @JsonProperty
    private List<RsDelvisFravaer> delvisFravaersListe;

    public List<RsPeriode> getFravaersPerioder() {
        return Objects.requireNonNullElse(fravaersPerioder, Collections.emptyList());
    }

    public List<RsDelvisFravaer> getDelvisFravaersListe() {
        return Objects.requireNonNullElse(delvisFravaersListe, Collections.emptyList());
    }
}
