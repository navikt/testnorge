package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FullmaktDTO extends DbVersjonDTO {

    private PersonRequestDTO nyFullmektig;

    private String motpartsPersonident;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
    private List<String> omraader;

    private Boolean eksisterendePerson;

    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }

    @JsonIgnore
    public String getIdentForRelasjon() {
        return motpartsPersonident;
    }
}
