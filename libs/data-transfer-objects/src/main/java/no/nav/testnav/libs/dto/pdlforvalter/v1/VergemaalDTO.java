package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @Builder.Default
    private List<TjenesteomraadeDTO> tjenesteomraade = new ArrayList<>();

    private Boolean eksisterendePerson;

    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }

    @JsonIgnore
    public String getIdentForRelasjon() {
        return vergeIdent;
    }
}
