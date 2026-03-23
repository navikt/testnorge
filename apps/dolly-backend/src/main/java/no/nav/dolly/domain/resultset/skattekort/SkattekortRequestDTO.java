package no.nav.dolly.domain.resultset.skattekort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.skattekort.domain.ArbeidstakerSkatt;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortRequestDTO {

    private List<ArbeidstakerSkatt> arbeidsgiverSkatt;

    public List<ArbeidstakerSkatt> getArbeidsgiverSkatt() {

        if (isNull(arbeidsgiverSkatt)) {

            arbeidsgiverSkatt = new ArrayList<>();
        }
        return arbeidsgiverSkatt;
    }
}
