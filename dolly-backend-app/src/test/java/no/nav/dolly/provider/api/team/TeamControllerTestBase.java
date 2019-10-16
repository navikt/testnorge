package no.nav.dolly.provider.api.team;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUtvidet;
import no.nav.dolly.provider.RestTestBase;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

abstract class TeamControllerTestBase extends RestTestBase {

    static final String ENDPOINT_BASE_URI = "/api/v1/team";

    void verifyTeamSize(Team team) {
        RsTeamUtvidet resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + team.getId())
                .andExpect(HttpStatus.OK, RsTeamUtvidet.class);
        assertThat(resp.getAntallMedlemmer(), is(team.getMedlemmer().size()));
    }
}
