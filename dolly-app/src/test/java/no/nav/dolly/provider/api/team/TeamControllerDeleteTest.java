package no.nav.dolly.provider.api.team;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;

@DisplayName("DELETE /api/v1/team")
class TeamControllerDeleteTest extends TeamControllerTestBase {

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found ved sletting av ikke-eksisterende team")
    void shouldFail404WhenDeleteUnregisteredTeam() {
        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Team med id 123 ble ikke funnet."));
    }

    @Test
    @DisplayName("Sletter eksisterende team")
    void shouldDeleteTeam() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker);
        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + team.getId())
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        //Verifiserer sletting
        sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + team.getId())
                .andExpect(HttpStatus.NOT_FOUND, RsTeamUtvidet.class);
    }

    @Test
    @DisplayName("Fjerner et enkelt medlem fra eksisterende team")
    void shouldRemoveSingleMemberFromTeam() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Bruker bruker2 = dataFactory.createBruker(NAV_IDENT + "2");
        Bruker bruker3 = dataFactory.createBruker(NAV_IDENT + "3");
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker, bruker2, bruker3);

        verifyTeamSize(team);

        RsTeamUtvidet respUpdate = sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + team.getId() + "/deleteMedlem?navIdent=" + bruker3.getNavIdent())
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);

        assertThat(respUpdate.getAntallMedlemmer(), is(2));
    }
}
