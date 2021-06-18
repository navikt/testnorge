package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilierelasjonDTO extends DbVersjonDTO {

    private ROLLE minRolleForPerson;
    private String relatertPerson;
    private ROLLE relatertPersonsRolle;
    public enum ROLLE {MOR, FAR, MEDMOR, BARN}
}
