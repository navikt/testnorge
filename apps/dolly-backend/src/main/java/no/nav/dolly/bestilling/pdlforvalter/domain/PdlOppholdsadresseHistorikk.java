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
public class PdlOppholdsadresseHistorikk {

    private List<PdlOppholdsadresse> pdlAdresser;

    public List<PdlOppholdsadresse> getPdlAdresser() {
        if (isNull(pdlAdresser)) {
            pdlAdresser = new ArrayList<>();
        }
        return pdlAdresser;
    }
}
