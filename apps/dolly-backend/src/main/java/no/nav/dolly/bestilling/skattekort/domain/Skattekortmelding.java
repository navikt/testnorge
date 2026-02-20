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
public class Skattekortmelding {

    private Resultatstatus resultatPaaForespoersel;
    private Skattekort skattekort;
    private List<Tilleggsopplysning> tilleggsopplysning;
    private Integer inntektsaar;

    public List<Tilleggsopplysning> getTilleggsopplysning() {

        if (isNull(tilleggsopplysning)) {
            tilleggsopplysning = new ArrayList<>();
        }
        return tilleggsopplysning;
    }
}