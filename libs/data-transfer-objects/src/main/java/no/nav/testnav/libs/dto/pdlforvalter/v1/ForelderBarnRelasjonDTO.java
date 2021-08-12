package no.nav.testnav.libs.dto.pdlforvalter.v1;

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

    private ROLLE minRolleForPerson;
    private String relatertPerson;
    private ROLLE relatertPersonsRolle;

    private Boolean borIkkeSammen;
    private PersonRequestDTO nyRelatertPerson;
    private Boolean partnerErIkkeForelder;

    public enum ROLLE {BARN, FORELDER, MOR, FAR, MEDMOR}
}
