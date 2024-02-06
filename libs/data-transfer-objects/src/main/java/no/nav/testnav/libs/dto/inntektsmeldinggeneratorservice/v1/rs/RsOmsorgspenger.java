package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
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
