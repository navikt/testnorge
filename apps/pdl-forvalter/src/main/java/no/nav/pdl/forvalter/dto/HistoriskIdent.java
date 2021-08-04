package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoriskIdent extends DbVersjonDTO {

    private List<String> identer;

    public List<String> getIdenter() {
        if (isNull(identer)) {
            identer = new ArrayList<>();
        }
        return identer;
    }
}