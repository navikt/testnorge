package no.nav.regression.scenarios.rest.testgruppe;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultSet.RsOpprettTestgruppe;
import no.nav.dolly.testdata.builder.RsOpprettTestgruppeBuilder;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OpprettTestgruppeScenarios extends TestgruppeTestCaseBase {

    @Test
    public void opprettBrukerBasertPaaCurrentBruker() throws Exception {
        Team team = teamRepository.findAll().get(0);

        RsOpprettTestgruppe rsOpprettTestgruppe = RsOpprettTestgruppeBuilder.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .teamId(team.getId())
                .build()
                .convertToRealRsOpprettTestgruppe();

        String url = endpointUrl;

        MvcResult mvcResult = mvcMock.perform(post(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(rsOpprettTestgruppe)))
                .andExpect(status().isCreated())
                .andReturn();

        Testgruppe gruppe = testGruppeRepository.findByNavn("mingruppe");

        assertThat(gruppe.getId(), is(notNullValue()));
        assertThat(gruppe.getNavn(), is("mingruppe"));
        assertThat(gruppe.getHensikt(), is("hensikt"));
        assertThat(gruppe.getOpprettetAv().getNavIdent(), is(standardNavIdent));
    }
}
