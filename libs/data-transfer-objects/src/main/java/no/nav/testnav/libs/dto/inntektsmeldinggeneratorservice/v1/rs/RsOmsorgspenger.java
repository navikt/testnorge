package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
public class RsOmsorgspenger {

    private Boolean harUtbetaltPliktigeDager;
    private List<RsPeriode> fravaersPerioder;
    private List<RsDelvisFravaer> delvisFravaersListe;

    public List<RsPeriode> getFravaersPerioder() {
        return Objects.requireNonNullElse(fravaersPerioder, Collections.emptyList());
    }

    public List<RsDelvisFravaer> getDelvisFravaersListe() {
        return Objects.requireNonNullElse(delvisFravaersListe, Collections.emptyList());
    }
}
