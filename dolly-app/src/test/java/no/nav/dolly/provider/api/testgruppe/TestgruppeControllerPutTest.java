package no.nav.dolly.provider.api.testgruppe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.RsTestident;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@DisplayName("PUT /api/v1/gruppe")
class TestgruppeControllerPutTest extends TestgruppeTestCaseBase {

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found når Testgruppe ikke finnes")
    void shouldFail404WhenTestgruppeDontExist() {

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        LinkedHashMap resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Finner ikke gruppe basert på gruppeID: 123"));
    }

    @Test
    @DisplayName("Oppdaterer informasjon om Testgruppe")
    void updateTestgruppe() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");
        Team team = dataFactory.createTeam("Teamnavn");

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .teamId(team.getId())
                .build();

        RsTestgruppeUtvidet resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, RsTestgruppeUtvidet.class);

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
        assertThat(resp.getTeam().getNavn(), is("Teamnavn"));
    }

    @Test
    @DisplayName("Fjerner TestIdenter fra Testgruppe")
    void removeTestidents() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        List<RsTestident> testidents = testgruppe.getTestidenter().stream()
                .map(t -> RsTestident.builder().ident(t.getIdent()).build())
                .collect(Collectors.toList());

       sendRequest(testidents)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/slettTestidenter")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        RsTestgruppeUtvidet resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, RsTestgruppeUtvidet.class);

        assertThat(resp.getTestidenter().size(), is(0));
    }

}
