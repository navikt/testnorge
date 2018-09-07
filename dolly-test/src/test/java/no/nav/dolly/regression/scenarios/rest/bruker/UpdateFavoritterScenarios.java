package no.nav.dolly.regression.scenarios.rest.bruker;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.RsBruker;
import no.nav.dolly.domain.resultset.RsBrukerUpdateFavoritterReq;

import java.util.Arrays;
import java.util.HashSet;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateFavoritterScenarios extends BrukerTestCaseBase {

    @Test
    public void leggTilFavorittTilBruker() throws Exception {
        RsBrukerUpdateFavoritterReq request = new RsBrukerUpdateFavoritterReq();
        request.setGruppeId(standardTestgruppe.getId());

        MvcResult mvcResult = mvcMock.perform(put(endpointUrl + "/" + "leggTilFavoritt")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        RsBruker responseBruker = convertMvcResultToObject(mvcResult, RsBruker.class);

        assertThat(responseBruker.getNavIdent(), is(standardNavIdent));
        assertThat(responseBruker.getTeams().size(), is(1));
        assertThat(responseBruker.getFavoritter().size(), is(1));
    }

    @Test
    public void fjernEnFavorittFraBruker() throws Exception {
        Bruker foerAddBruker = brukerRepository.findBrukerByNavIdent(standardNavIdent);
        foerAddBruker.setFavoritter(new HashSet<>(Arrays.asList(standardTestgruppe)));
        Bruker foerFjernBruker = brukerRepository.save(foerAddBruker);
        assertThat(foerFjernBruker.getFavoritter().size(), is(1));

        RsBrukerUpdateFavoritterReq request = new RsBrukerUpdateFavoritterReq();
        request.setGruppeId(standardTestgruppe.getId());

        MvcResult mvcResult = mvcMock.perform(put(endpointUrl + "/" + "fjernFavoritt")
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(request)))
                .andExpect(status().isOk())
                .andReturn();

        RsBruker res = convertMvcResultToObject(mvcResult, RsBruker.class);

        assertThat(res.getNavIdent(), is(standardNavIdent));
        assertThat(res.getTeams().size(), is(1));
        assertThat(res.getFavoritter().size(), is(0));
    }

}
