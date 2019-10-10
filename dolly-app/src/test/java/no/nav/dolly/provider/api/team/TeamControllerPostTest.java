package no.nav.dolly.provider.api.team;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.team.RsOpprettTeam;
import no.nav.dolly.domain.resultset.entity.team.RsTeamUtvidet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;

@DisplayName("POST /api/v1/team")
class TeamControllerPostTest extends TeamControllerTestBase {

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found ved oppretting av team når eier (Bruker) ikke finnes")
    void shouldFail404WhenCreatingTeamUnregistered() {
        RsOpprettTeam newTeam = RsOpprettTeam.builder()
                .navn("Created team")
                .beskrivelse("Nytt team").build();
        LinkedHashMap resp = sendRequest(newTeam)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Bruker ikke funnet"));
    }

    @Test
    @DisplayName("Returnerer HTTP 500 Internal Server Error ved persistering av team på grunn av database constraint")
    void shouldFailDbConstraintCreateTeam() {
        dataFactory.createBruker(NAV_IDENT);

        LinkedHashMap resp = sendRequest(new RsOpprettTeam())
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.INTERNAL_SERVER_ERROR, LinkedHashMap.class);

        assertTrue(getErrMsg(resp).contains("could not execute statement; SQL "));
    }

    @Test
    @DisplayName("Oppretter et team")
    void shouldCreateTeam() {
        Bruker bruker = dataFactory.createBruker(NAV_IDENT);

        RsOpprettTeam newTeam = RsOpprettTeam.builder()
                .navn("Created team")
                .beskrivelse("Nytt team").build();
        RsTeamUtvidet ts = sendRequest(newTeam)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.CREATED, RsTeamUtvidet.class);

        assertNotNull(ts);
        assertThat(ts.getNavn(), is(newTeam.getNavn()));
        assertThat(ts.getEierNavIdent(), is(bruker.getNavIdent()));
        assertThat(ts.getAntallMedlemmer(), is(1));
        assertThat(ts.getMedlemmer().get(0).getNavIdent(), is(bruker.getNavIdent()));
    }
}
