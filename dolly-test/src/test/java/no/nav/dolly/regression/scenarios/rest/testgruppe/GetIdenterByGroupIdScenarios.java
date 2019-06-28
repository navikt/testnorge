package no.nav.dolly.regression.scenarios.rest.testgruppe;

import static org.assertj.core.util.Sets.newHashSet;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.testdata.builder.TestidentBuilder;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class GetIdenterByGroupIdScenarios extends TestgruppeTestCaseBase {

    @Test
    public void getIdenterByGroupId_returnererAlleTestidenterIStringformat() throws Exception {
        Testgruppe testgruppe = gruppeRepository.findAllByOrderByNavn().get(0);
        Long gruppeId = testgruppe.getId();
        String ident1 = "10";
        String ident2 = "20";

        Testident t1 = TestidentBuilder.builder().ident(ident1).testgruppe(testgruppe).build().convertToRealTestident();
        Testident t2 = TestidentBuilder.builder().ident(ident2).testgruppe(testgruppe).build().convertToRealTestident();
        Testident testident1 = identRepository.save(t1);
        Testident testident2 = identRepository.save(t2);

        Set gruppe = newHashSet(Arrays.asList(testident1, testident2));
        testgruppe.setTestidenter(gruppe);
        gruppeRepository.save(testgruppe);

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
        Testgruppe g1 = gruppeRepository.findAllByOrderByNavn().get(0);
        Long gruppeId = g1.getId();

        String url = endpointUrl + "/" + gruppeId + "/identer";
        MvcResult mvcResult = mvcMock.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        List<String> resultat = convertMvcResultToList(mvcResult, String.class);

        assertThat(resultat.isEmpty(), is(true));
    }
}

