package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlFamilierelasjon {

    private String kilde;
    private ROLLE minRolleForPerson;
    private String relatertPerson;
    private ROLLE relatertPersonsRolle;
    public enum ROLLE {MOR, FAR, MEDMOR, BARN}
}
