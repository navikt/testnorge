package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidstakerSkatt {

    private List<Skattekortmelding> arbeidstaker;

    public List<Skattekortmelding> getArbeidstaker() {

        if (isNull(arbeidstaker)) {
            arbeidstaker = new ArrayList<>();
        }
        return arbeidstaker;
    }
}
