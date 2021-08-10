package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
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
