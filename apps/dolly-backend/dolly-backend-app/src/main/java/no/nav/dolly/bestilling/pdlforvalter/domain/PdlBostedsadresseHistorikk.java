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
public class PdlBostedsadresseHistorikk {

    private List<PdlBostedadresse> pdlAdresser;

    public List<PdlBostedadresse> getPdlAdresser() {
        if (isNull(pdlAdresser)) {
            pdlAdresser = new ArrayList<>();
        }
        return pdlAdresser;
    }
}
