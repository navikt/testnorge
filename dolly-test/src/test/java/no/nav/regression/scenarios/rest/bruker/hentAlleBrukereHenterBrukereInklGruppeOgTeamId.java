package no.nav.regression.scenarios.rest.bruker;

import no.nav.resultSet.RsBrukerTeamAndGruppeIDs;

import java.util.List;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class hentAlleBrukereHenterBrukereInklGruppeOgTeamId extends BrukerTestCaseBase{

    @Test
    public void getBruker() throws Exception {

        MvcResult mvcResult = mvcMock.perform(get(endpointUrl))
                .andExpect(status().isOk())
                .andReturn();

        List<RsBrukerTeamAndGruppeIDs> resultat = convertMvcResultToList(mvcResult, RsBrukerTeamAndGruppeIDs.class);

        RsBrukerTeamAndGruppeIDs res = resultat.get(0);

        assertThat(resultat.size(), is(1));
        assertThat(res.getNavIdent(), is(standardNavIdent));
        assertThat(res.getTeams().size(), is(1));
        assertThat(res.getTeams().get(0).getNavn(), is(standardTeamnavn));
    }
}
