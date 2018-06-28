package no.nav.regression.scenarios.rest.testgruppe;

import no.nav.dolly.testdata.builder.RsOpprettTestgruppeBuilder;
import no.nav.jpa.Testgruppe;
import no.nav.resultSet.RsOpprettTestgruppe;

import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

public class OpprettTestgruppeBasertPaaCurrentUser extends TestgruppeTestCaseBase {

    @Test
    public void opprettBrukerBasertPaaCurrentBruker() throws Exception {
        RsOpprettTestgruppe rsOpprettTestgruppe = RsOpprettTestgruppeBuilder.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build()
                .convertToRealRsOpprettTestgruppe();

        String url = endpointUrl + "/team/" + standardTeam.getId();

        MvcResult mvcResult = mvcMock.perform(post(url)
                .contentType(APPLICATION_JSON_UTF8)
                .content(convertObjectToJson(rsOpprettTestgruppe)))
                .andExpect(status().isCreated())
                .andReturn();

        Testgruppe gruppe = testGruppeRepository.findByNavn("mingruppe");

        assertThat(gruppe.getId(), is(notNullValue()));
        assertThat(gruppe.getNavn(), is("mingruppe"));
        assertThat(gruppe.getHensikt(), is("hensikt"));
        assertThat(gruppe.getOpprettetAv().getNavIdent(), is(standardNavnIdent));
    }
}
