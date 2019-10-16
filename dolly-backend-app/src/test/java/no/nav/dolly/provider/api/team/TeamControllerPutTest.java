package no.nav.dolly.provider.api.team;


import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUtvidet;
import no.nav.dolly.common.RsTeamUtvidetBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;

@DisplayName("PUT /api/v1/team")
class TeamControllerPutTest extends TeamControllerTestBase {

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found ved registrering av medlemmer i et team som ikke finnes")
    void shouldFail404AddMembersToNonExistingTeam() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Bruker bruker2 = dataFactory.createBruker(NAV_IDENT + "2");

        LinkedHashMap resp = sendRequest(asList(bruker.getNavIdent(), bruker2.getNavIdent()))
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/123/leggTilMedlemmer")
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Team ikke funnet for denne IDen: 123"));
    }

    @Test
    @DisplayName("Legger til medlemmer i et eksisterende team")
    void shouldAddMembersToTeam() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Bruker bruker2 = dataFactory.createBruker(NAV_IDENT + "2");
        Bruker bruker3 = dataFactory.createBruker(NAV_IDENT + "3");
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker);

        verifyTeamSize(team);

        RsTeamUtvidet respUpdate = sendRequest(asList(bruker2.getNavIdent(), bruker3.getNavIdent()))
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + team.getId() + "/leggTilMedlemmer")
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);

        assertThat(respUpdate.getAntallMedlemmer(), is(3));
    }

    @Test
    @DisplayName("Fjerner flere medlemmer fra et team")
    void shouldRemoveMultipleMembersFromTeam() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Bruker bruker2 = dataFactory.createBruker(NAV_IDENT + "2");
        Bruker bruker3 = dataFactory.createBruker(NAV_IDENT + "3");
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker, bruker2, bruker3);

        verifyTeamSize(team);

        RsTeamUtvidet respUpdate = sendRequest(asList(bruker2.getNavIdent(), bruker3.getNavIdent()))
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + team.getId() + "/fjernMedlemmer")
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);

        assertThat(respUpdate.getAntallMedlemmer(), is(1));
    }

    @Test
    @DisplayName("Endrer informasjon på eksisterende team")
    void shouldChangeTeamInfo() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Bruker bruker2 = dataFactory.createBruker(NAV_IDENT + "2");
        Bruker bruker3 = dataFactory.createBruker(NAV_IDENT + "3");
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker, bruker2, bruker3);

        //Verifisere opprinnelig info
        RsTeamUtvidet resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + team.getId())
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);
        assertThat(resp.getNavn(), is(team.getNavn()));


        RsTeamUtvidet endretTeam = RsTeamUtvidetBuilder.builder()
                .navn("Endret team")
                .build().convertToRealRsTeam();

        RsTeamUtvidet respUpdate = sendRequest(endretTeam)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + team.getId())
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);

        assertThat(respUpdate.getNavn(), is(endretTeam.getNavn()));
    }
}
