package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

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
