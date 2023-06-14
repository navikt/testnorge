package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ForelderBarnRelasjonDTO extends DbVersjonDTO {

    private Rolle minRolleForPerson;
    private String relatertPerson;
    private Rolle relatertPersonsRolle;

    private RelatertBiPersonDTO relatertPersonUtenFolkeregisteridentifikator;

    private Boolean borIkkeSammen;
    private PersonRequestDTO nyRelatertPerson;
    private Boolean partnerErIkkeForelder;

    private Boolean eksisterendePerson;

    private DeltBostedDTO deltBosted;

    public boolean isEksisterendePerson() {

        return isTrue(eksisterendePerson);
    }

    @JsonIgnore
    public boolean hasBarn() {

        return Rolle.BARN == relatertPersonsRolle;
    }

    @JsonIgnore
    public boolean hasBarnWithIdent() {

        return Rolle.BARN == relatertPersonsRolle && isNotBlank(relatertPerson);
    }

    @JsonIgnore
    public boolean isForeldre() {

        return Rolle.FORELDER == minRolleForPerson ||
                Rolle.MOR == minRolleForPerson ||
                Rolle.FAR == minRolleForPerson ||
                Rolle.MEDMOR == minRolleForPerson;
    }

    @JsonIgnore
    public boolean isBarn() {

        return Rolle.BARN == minRolleForPerson;
    }

    @JsonIgnore
    public boolean isRelatertMedIdentifikator() {

        return isNotBlank(relatertPerson);
    }

    public enum Rolle {BARN, FORELDER, MOR, FAR, MEDMOR}

    @JsonIgnore
    public String getIdentForRelasjon() {
        return relatertPerson;
    }
}
