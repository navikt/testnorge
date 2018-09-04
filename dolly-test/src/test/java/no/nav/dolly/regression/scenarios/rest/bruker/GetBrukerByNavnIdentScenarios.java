package no.nav.dolly.regression.scenarios.rest.bruker;


import no.nav.dolly.domain.resultset.RsBrukerTeamAndGruppeIDs;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetBrukerByNavnIdentScenarios extends BrukerTestCaseBase {

    @Test
    public void happyPath() throws Exception {
        MvcResult mvcResult = mvcMock.perform(get(endpointUrl + "/" + standardNavIdent))
                .andExpect(status().isOk())
                .andReturn();

        RsBrukerTeamAndGruppeIDs res = convertMvcResultToObject(mvcResult, RsBrukerTeamAndGruppeIDs.class);

        assertThat(res.getNavIdent(), is(standardNavIdent));
        assertThat(res.getTeams().size(), is(1));
        assertThat(res.getTeams().get(0).getNavn(), is(standardTeamnavn));
    }

    @Test
    public void whenNoBrukerExistsExceptionNotFoundThrownAndStatusCode404() throws Exception {
        MvcResult mvcResult = mvcMock.perform(get(endpointUrl + "/" + "finnesIkke"))
                .andExpect(status().isNotFound())
                .andReturn();
    }

}
