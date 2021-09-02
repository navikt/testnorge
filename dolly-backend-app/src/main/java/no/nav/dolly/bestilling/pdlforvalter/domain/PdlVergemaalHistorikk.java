package no.nav.dolly.bestilling.pdlforvalter.domain;

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
public class PdlVergemaalHistorikk {

    private List<PdlVergemaal> vergemaaler;

    public List<PdlVergemaal> getVergemaaler() {
        if (isNull(vergemaaler)) {
            vergemaaler = new ArrayList<>();
        }
        return vergemaaler;
    }
}
