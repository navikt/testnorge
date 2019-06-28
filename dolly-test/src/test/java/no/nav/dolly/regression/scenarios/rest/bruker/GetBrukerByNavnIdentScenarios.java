package no.nav.dolly.regression.scenarios.rest.bruker;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.nav.dolly.domain.resultset.RsBrukerTeamAndGruppeIDs;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

public class GetBrukerByNavnIdentScenarios extends BrukerTestCaseBase {

    @Test
    public void happyPath() throws Exception {
        MvcResult mvcResult = mvcMock.perform(get(endpointUrl + "/" + STANDARD_NAV_IDENT))
                .andExpect(status().isOk())
                .andReturn();

        RsBrukerTeamAndGruppeIDs res = convertMvcResultToObject(mvcResult, RsBrukerTeamAndGruppeIDs.class);

        assertThat(res.getNavIdent(), is(STANDARD_NAV_IDENT));
        assertThat(res.getTeams().size(), is(1));
        assertThat(res.getTeams().get(0).getNavn(), is(STANDARD_TEAM_NAVN));
    }

    @Test
    public void whenNoBrukerExistsExceptionNotFoundThrownAndStatusCode404() throws Exception {
        MvcResult mvcResult = mvcMock.perform(get(endpointUrl + "/" + "finnesIkke"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}
