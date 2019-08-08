package no.nav.dolly.provider.api.team;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsTeam;
import no.nav.dolly.domain.resultset.RsTeamUtvidet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;

@DisplayName("GET /api/v1/team")
class TeamControllerGetTest extends TeamControllerTestBase {

    private static final ParameterizedTypeReference<List<RsTeam>> expectedResponseType = new ParameterizedTypeReference<List<RsTeam>>() {
    };

    @Test
    @DisplayName("Returnerer HTTP 403 Forbidden når Authorization-headeren mangler")
    void shouldFailMissingHeader() {
        sendRequest()
                .withoutHeader(HttpHeaders.AUTHORIZATION)
                .to(HttpMethod.GET, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.FORBIDDEN, String.class);
    }

    @Test
    @DisplayName("Returnerer en tom liste når navIdent ikke sendes med")
    void shouldGetEmptyListWithoutNavIdent() {
        List<RsTeam> rsTeams = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI)
                .andExpectList(HttpStatus.OK, expectedResponseType);

        assertThat(rsTeams.size(), is(0));
    }

    @Test
    @DisplayName("Returnerer bare team hvor navIdent er registert som medlem")
    void shouldGetTeamsWhereNavIdentIsMember() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Bruker bruker2 = dataFactory.createBruker(NAV_IDENT + "2");
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker);
        dataFactory.createTeam(bruker, "Team 2", "Nytt team", bruker2);

        List<RsTeam> rsTeams = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "?navIdent=" + NAV_IDENT)
                .andExpectList(HttpStatus.OK, expectedResponseType);

        assertThat(rsTeams.size(), is(1));
        assertThat(rsTeams.get(0).getNavn(), is(team.getNavn()));
    }

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found når team som ikke finnes")
    void shouldFail404GetTeamByTeamId() {

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Team ikke funnet for denne IDen: 123"));
    }

    @Test
    @DisplayName("Returnerer team som har innsendt teamId")
    void shouldGetTeamByTeamId() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);
        Team team = dataFactory.createTeam(bruker, "Team", "Nytt team", bruker);

        RsTeamUtvidet resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + team.getId())
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);

        assertNotNull(resp);
        assertThat(resp.getNavn(), is(team.getNavn()));
        assertThat(resp.getAntallMedlemmer(), is(team.getMedlemmer().size()));
    }

}
