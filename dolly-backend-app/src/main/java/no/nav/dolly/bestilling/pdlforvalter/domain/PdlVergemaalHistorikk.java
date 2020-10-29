package no.nav.dolly.bestilling.pdlforvalter.domain;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlVergemaalHistorikk {

    private List<PdlVergemaal> vergemaaler;

    public List<PdlVergemaal> getVergemaaler() {
        if (isNull(vergemaaler)) {
            vergemaaler = new ArrayList<>();
        }
        return vergemaaler;
    }
}
