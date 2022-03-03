package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VergemaalDTO extends DbVersjonDTO {

    private VergemaalEmbete vergemaalEmbete;
    private VergemaalSakstype sakType;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;

    private PersonRequestDTO nyVergeIdent;
    private String vergeIdent;
    private VergemaalMandattype mandatType;

    private Boolean eksisterendePerson;

    @JsonIgnore
    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }
}
