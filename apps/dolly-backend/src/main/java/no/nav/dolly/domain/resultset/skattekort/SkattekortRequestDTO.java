package no.nav.dolly.domain.resultset.skattekort;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.skattekortservice.v1.Arbeidsgiver;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortRequestDTO {

    private List<Arbeidsgiver> arbeidsgiverSkatt;

    public List<Arbeidsgiver> getArbeidsgiverSkatt() {

        if (isNull(arbeidsgiverSkatt)) {

            arbeidsgiverSkatt = new ArrayList<>();
        }
        return arbeidsgiverSkatt;
    }
}
