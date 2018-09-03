package no.nav.dolly.regression.scenarios.rest.bruker;


import no.nav.dolly.domain.resultset.RsBruker;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class getCurrentBrukerScenarios extends BrukerTestCaseBase{

    @Test
    public void getBrukerBasertPaaInloggetBrukerFraOIDC() throws Exception {
        MvcResult mvcResult = mvcMock.perform(get(endpointUrl + "/" + "current"))
                .andExpect(status().isOk())
                .andReturn();

        RsBruker res = convertMvcResultToObject(mvcResult, RsBruker.class);

        assertThat(res.getNavIdent(), is(standardNavIdent));
    }
}
