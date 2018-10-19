package no.nav.dolly.regression.scenarios.rest.testgruppe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.testdata.builder.TestidentBuilder;

public class GetIdenterByGroupIdScenarios extends TestgruppeTestCaseBase {

    @Test
    public void getIdenterByGroupId_returnererAlleTestidenterIStringformat() throws Exception {
        Testgruppe g1 = gruppeRepository.findAll().get(0);
        Long gruppeId = g1.getId();
        String ident1 = "1";
        String ident2 = "2";

        Testident t1 = TestidentBuilder.builder().ident(ident1).testgruppe(g1).build().convertToRealTestident();
        Testident t2 = TestidentBuilder.builder().ident(ident2).testgruppe(g1).build().convertToRealTestident();
        Testident testident1 = identRepository.save(t1);
        Testident testident2 = identRepository.save(t2);

        HashSet gruppe = new HashSet(Arrays.asList(testident1, testident2));
        g1.setTestidenter(gruppe);
        gruppeRepository.save(g1);

        String url = endpointUrl + "/" + gruppeId + "/identer";
        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        List<String> resultat = convertMvcResultToList(mvcResult, String.class);

        assertThat(resultat.size(), is(2));
        assertThat(resultat.contains(ident1), is(true));
        assertThat(resultat.contains(ident2), is(true));
    }

    @Test
    public void getIdenterByGroupId_handtererTommeGrupper() throws Exception {
        Testgruppe g1 = gruppeRepository.findAll().get(0);
        Long gruppeId = g1.getId();

        String url = endpointUrl + "/" + gruppeId + "/identer";
        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        List<String> resultat = convertMvcResultToList(mvcResult, String.class);

        assertThat(resultat.isEmpty(), is(true));
    }
}

