package no.nav.registre.sdForvalter.database;

import no.nav.registre.sdForvalter.database.model.Team;
import no.nav.registre.sdForvalter.database.model.Varighet;

public interface Ownable {

    Team getTeam();

    void setTeam(Team team);

    Varighet getVarighet();

    void setVarighet(Varighet varighet);

}
