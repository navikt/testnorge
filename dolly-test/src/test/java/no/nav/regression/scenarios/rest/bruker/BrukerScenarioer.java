package no.nav.regression.scenarios.rest.bruker;

import no.nav.dolly.testdata.builder.RsBrukerBuilder;
import no.nav.jpa.Bruker;
import no.nav.resultSet.RsBruker;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class BrukerScenarioer extends BrukerTestCaseBase{

    @Test
    public void saveBruker() throws Exception {

        RsBruker request = RsBrukerBuilder.builder().navIdent("bruker").build().convertToRealRsBruker();

        MvcResult mvcResult = mvcMock.perform(post(endpointUrl)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Bruker bruker = brukerRepository.findAll().get(0);

        assertThat(bruker.getNavIdent(), is("bruker"));
    }
}
