package no.nav.dolly.provider.api.testgruppe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@DisplayName("POST /api/v1/gruppe")
class TestgruppeControllerPostTest extends TestgruppeTestCaseBase {

    @Test
    void opprettTestgruppeBasertPaaCurrentBruker() {
        Team team = dataFactory.createTeam("Teamnavn");

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .teamId(team.getId())
                .build();

        RsTestgruppeUtvidet resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.CREATED, RsTestgruppeUtvidet.class);

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
        assertThat(resp.getOpprettetAvNavIdent(), is("NAVIDENT"));
    }

    @Test
    void opprettTestgruppeUtenAaSpesifisereTeamOgFaaSpesifisertTeamMedNavidentNavn() {
        dataFactory.createBruker("NAVIDENT");

        RsOpprettEndreTestgruppe rsOpprettTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        RsTestgruppeUtvidet resp = sendRequest(rsOpprettTestgruppe)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.CREATED, RsTestgruppeUtvidet.class);

        RsTeamMedIdOgNavn team = resp.getTeam();

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
        assertThat(team.getNavn(), is("NAVIDENT"));
    }
}
