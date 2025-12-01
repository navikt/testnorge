package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO;

import java.util.List;

import static java.util.Objects.nonNull;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OpprettIdent extends DbVersjonDTO {

    private List<String> historiskeIdenter;
    private boolean opphoert;

    public List<String> getHistoriskeIdenter() {

        return nonNull(historiskeIdenter) && !historiskeIdenter.isEmpty() ?
                historiskeIdenter : null;
    }
}