package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOpprettPerson extends DbVersjonDTO {

    private List<String> historiskeIdenter;

    public List<String> getHistoriskeIdenter() {
        if (isNull(historiskeIdenter)) {
            return new ArrayList<>();
        }
        return historiskeIdenter;
    }
}
