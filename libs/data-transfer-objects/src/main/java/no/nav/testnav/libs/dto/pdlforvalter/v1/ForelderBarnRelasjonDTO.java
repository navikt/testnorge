package no.nav.testnav.libs.dto.pdlforvalter.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ForelderBarnRelasjonDTO extends DbVersjonDTO {

    private Rolle minRolleForPerson;
    private String relatertPerson;
    private Rolle relatertPersonsRolle;

    private Boolean borIkkeSammen;
    private PersonRequestDTO nyRelatertPerson;
    private Boolean partnerErIkkeForelder;

    private Boolean eksisterendePerson;

    public enum Rolle {BARN, FORELDER, MOR, FAR, MEDMOR}

    @JsonIgnore
    public boolean hasBarn() {

        return Rolle.BARN == relatertPersonsRolle;
    }
}
