package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VergemaalDTO extends DbVersjonDTO {

    private VergemaalEmbete vergemaalEmbete;
    private VergemaalSakstype sakType;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;

    private PersonRequestDTO nyVergeIdent;
    private String vergeIdent;
    private VergemaalMandattype mandatType;

    private Boolean eksisterendePerson;

    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }
}
