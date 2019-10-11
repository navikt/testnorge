package no.nav.dolly.provider.api.testgruppe;

import static org.hamcrest.CoreMatchers.both;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.jupiter.api.Assertions.assertTrue;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeUtvidet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.LinkedHashMap;
import java.util.List;

@DisplayName("GET /api/v1/gruppe")
class TestgruppeControllerGetTest extends TestgruppeTestBase {

    private static final ParameterizedTypeReference<List<String>> expectedResponseString = new ParameterizedTypeReference<List<String>>() {
    };
    private static final ParameterizedTypeReference<List<RsTestgruppe>> expectedResponseRsTestgruppe = new ParameterizedTypeReference<List<RsTestgruppe>>() {
    };

    @Test
    @DisplayName("Returnerer Testgrupper tilknyttet til navIdent men uten spesifikk teamId")
    void shouldGetTestgrupperWithNavIdentWithoutTeamId() {
        Bruker bruker = dataFactory.createBruker("NAVIDENT");
        Bruker annenBruker = dataFactory.createBruker("OTHER");

        Team team = dataFactory.createTeam(annenBruker, "Team", "", annenBruker, bruker);
        Team team2 = dataFactory.createTeam(annenBruker, "Team2", "", annenBruker);

        Testgruppe testgruppe = dataFactory.createTestgruppe("gruppe", bruker, team);
        Testgruppe testgruppe2 = dataFactory.createTestgruppe("gruppe2", annenBruker, team2);
        Testgruppe testgruppe3 = dataFactory.createTestgruppe("gruppe3", annenBruker, team2);

        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe.getId());
        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe2.getId());
        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe3.getId());

        String url = UriComponentsBuilder.fromUriString(ENDPOINT_BASE_URI).queryParam("navIdent", bruker.getNavIdent()).toUriString();
        List<RsTestgruppe> resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpectList(HttpStatus.OK, expectedResponseRsTestgruppe);

        assertThat(resp.size(), is(3));

        assertThat(resp, hasItem(
                hasProperty("navn", equalTo("gruppe")))
        );

        assertThat(resp, hasItem(both(
                hasProperty("navn", equalTo("gruppe3"))).and(
                hasProperty("opprettetAvNavIdent", equalTo(annenBruker.getNavIdent())))
        ));

        //Cleanup
        dataFactory.clearFavourites(bruker.getNavIdent());
    }

    @Test
    @DisplayName("Returnerer Testgrupper tilknyttet til navIdent gjennom favoritter og medlemskap")
    void shouldGetTestgrupperWithNavIdent() {
        Bruker bruker = dataFactory.createBruker("NAVIDENT");
        Bruker annenBruker = dataFactory.createBruker("OTHER");

        Team team = dataFactory.createTeam(annenBruker, "Team", "", annenBruker, bruker);
        Team team2 = dataFactory.createTeam(annenBruker, "Team2", "", annenBruker);

        Testgruppe testgruppe = dataFactory.createTestgruppe("gruppe", bruker, team);
        Testgruppe testgruppe2 = dataFactory.createTestgruppe("gruppe2", annenBruker, team2);
        Testgruppe testgruppe3 = dataFactory.createTestgruppe("gruppe3", annenBruker, team2);

        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe.getId());
        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe2.getId());
        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe3.getId());

        String url = UriComponentsBuilder.fromUriString(ENDPOINT_BASE_URI).queryParam("navIdent", bruker.getNavIdent()).queryParam("teamId", team2.getId()).toUriString();
        List<RsTestgruppe> resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpectList(HttpStatus.OK, expectedResponseRsTestgruppe);

        assertThat(resp.size(), is(2));

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
    @DisplayName("Returnerer Testgrupper tilknyttet teamId")
    void shouldGetTestgrupperWithTeamId() {
        Bruker bruker = dataFactory.createBruker("NAVIDENT");
        Bruker annenBruker = dataFactory.createBruker("OTHER");

        Team team = dataFactory.createTeam(annenBruker, "Team", "", annenBruker, bruker);
        Team team2 = dataFactory.createTeam(annenBruker, "Team2", "", annenBruker);

        Testgruppe testgruppe = dataFactory.createTestgruppe("gruppe", bruker, team);
        Testgruppe testgruppe2 = dataFactory.createTestgruppe("gruppe2", annenBruker, team2);
        Testgruppe testgruppe3 = dataFactory.createTestgruppe("gruppe3", annenBruker, team2);

        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe.getId());
        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe2.getId());
        dataFactory.addToBrukerFavourites(bruker.getNavIdent(), testgruppe3.getId());

        String url = ENDPOINT_BASE_URI + "?teamId=" + team2.getId();

        List<RsTestgruppe> resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpectList(HttpStatus.OK, expectedResponseRsTestgruppe);

        assertThat(resp.size(), is(2));

        //Cleanup
        dataFactory.clearFavourites(bruker.getNavIdent());
    }

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found  Testgruppe")
    void shouldFail404NotFound() {
        String url = ENDPOINT_BASE_URI + "/123";

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Finner ikke gruppe basert på gruppeID: 123"));
    }

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

        RsTestgruppeUtvidet resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpect(HttpStatus.OK, RsTestgruppeUtvidet.class);

        assertThat(resp.getNavn(), is("Test gruppe"));
        assertThat(resp.getAntallIdenter(), is(5)); //3 stk laget i createTestgruppe + 2 i addTestidenterToTestgruppe
    }

    @Disabled // todo Think this should be removed, as /identer endpoint isn't
    @Test
    @DisplayName("Returnerer alle Testidenter i en Testgruppe")
    void shouldReturnAllTestidentsInTestgruppe() {
        String ident1 = "10";
        String ident2 = "20";
        Testgruppe testgruppe = dataFactory.createTestgruppe("Test gruppe");

        Testident testident1 = dataFactory.createTestident(ident1, testgruppe);
        Testident testident2 = dataFactory.createTestident(ident2, testgruppe);

        testgruppe = dataFactory.addTestidenterToTestgruppe(testgruppe, testident1, testident2);

        String url = ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/identer";

        List<String> resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpectList(HttpStatus.OK, expectedResponseString);

        assertThat(resp.size(), is(5));
        assertTrue(resp.contains(ident1));
        assertTrue(resp.contains(ident2));
    }

    @Disabled // todo Think this should be removed, as /identer endpoint isn't
    @Test
    @DisplayName("Returnerer tom liste når Testgruppe ikke har registrerte Testidenter")
    void shouldReturnEmptyListWithTestidents() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        String url = ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/identer";

        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/slettTestident?ident=123")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);
        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/slettTestident?ident=234")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);
        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/slettTestident?ident=345")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        List<String> resp = sendRequest()
                .to(HttpMethod.GET, url)
                .andExpectList(HttpStatus.OK, expectedResponseString);

        assertTrue(resp.isEmpty());
    }
}

