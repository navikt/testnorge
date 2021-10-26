package no.nav.dolly.provider.api.testgruppe;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;

@DisplayName("GET /api/v1/gruppe")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class TestgruppeControllerGetTest extends TestgruppeTestBase {

    private static final ParameterizedTypeReference<List<RsTestgruppe>> expectedResponseRsTestgruppe =
            new ParameterizedTypeReference<List<RsTestgruppe>>() {
            };

    @Disabled
    @Test
    @DisplayName("Returnerer Testgrupper tilknyttet til brukerId gjennom favoritter og medlemskap")
    void shouldGetTestgrupperWithNavIdent() {
        Bruker bruker = dataFactory.createBruker("NAVIDENT");
        Bruker annenBruker = dataFactory.createBruker("OTHER");

        Testgruppe testgruppe = dataFactory.createTestgruppe("gruppe", bruker);
        Testgruppe testgruppe2 = dataFactory.createTestgruppe("gruppe2", annenBruker);
        Testgruppe testgruppe3 = dataFactory.createTestgruppe("gruppe3", annenBruker);

        dataFactory.addToBrukerFavourites(bruker.getBrukerId(), testgruppe.getId());
        dataFactory.addToBrukerFavourites(bruker.getBrukerId(), testgruppe2.getId());
        dataFactory.addToBrukerFavourites(bruker.getBrukerId(), testgruppe3.getId());

        String url = UriComponentsBuilder.fromUriString(ENDPOINT_BASE_URI).queryParam("brukerId", bruker.getNavIdent()).toUriString();
        List<RsTestgruppe> resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpectList(HttpStatus.OK, expectedResponseRsTestgruppe);

        assertThat(resp.size(), is(3));

        assertThat(resp, hasItem(both(
                hasProperty("navn", equalTo("gruppe2"))).and(
                hasProperty("opprettetAvNavIdent", equalTo(annenBruker.getNavIdent())))
        ));

        assertThat(resp, hasItem(both(
                hasProperty("navn", equalTo("gruppe3"))).and(
                hasProperty("opprettetAvNavIdent", equalTo(annenBruker.getNavIdent())))
        ));

        //Cleanup
        dataFactory.clearFavourites(bruker.getNavIdent());
    }

    @Test
    @DisplayName("Returnerer HTTP 200 med feilmelding Not Found i body")
    void shouldFail404NotFound() {
        String url = ENDPOINT_BASE_URI + "/123";

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Gruppe med id 123 ble ikke funnet."));
    }

    @Disabled
    @Test
    @DisplayName("Returnerer Testgruppe")
    void shouldReturnTestgruppe() {
        String ident1 = "10";
        String ident2 = "20";
        Testgruppe testgruppe = dataFactory.createTestgruppe("Test gruppe");

        Testident testident1 = dataFactory.createTestident(ident1, testgruppe);
        Testident testident2 = dataFactory.createTestident(ident2, testgruppe);

        testgruppe = dataFactory.addTestidenterToTestgruppe(testgruppe, testident1, testident2);

        String url = ENDPOINT_BASE_URI + "/" + testgruppe.getId();

        RsTestgruppeMedBestillingId resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpect(HttpStatus.OK, RsTestgruppeMedBestillingId.class);

        assertThat(resp.getNavn(), is("Test gruppe"));
        assertThat(resp.getAntallIdenter(), is(5)); //3 stk laget i createTestgruppe + 2 i addTestidenterToTestgruppe
    }
}

