package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ForeldreansvarDTO extends DbVersjonDTO {

    private Ansvar ansvar;
    private String ansvarlig;
    private PersonRequestDTO nyAnsvarlig;
    private RelatertBiPersonDTO ansvarligUtenIdentifikator;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;

    private Boolean eksisterendePerson;

    public enum Ansvar {FELLES, MOR, FAR, MEDMOR, ANDRE, UKJENT}

    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }

    @JsonIgnore
    public boolean isAnsvarligMedIdentifikator() {

        return isNotBlank(ansvarlig);
    }

    @JsonIgnore
    @Override
    public String getIdentForRelasjon() {
        return ansvarlig;
    }
}