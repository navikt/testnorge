package no.nav.registre.sdForvalter.database;

import no.nav.registre.sdForvalter.database.model.Team;

public interface Ownable {

    Team getTeam();

    void setTeam(Team team);

}
