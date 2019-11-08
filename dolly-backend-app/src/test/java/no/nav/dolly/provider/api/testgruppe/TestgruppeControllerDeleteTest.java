package no.nav.dolly.provider.api.testgruppe;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeUtvidet;

@DisplayName("DELETE /api/v1/gruppe")
class TestgruppeControllerDeleteTest extends TestgruppeTestBase {

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found ved sletting når Testgruppe ikke finnes")
    void shouldFail404WhenTestgruppeDontExist() {
        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Finner ikke gruppe basert på gruppeID: 123"));
    }

    @Test
    @DisplayName("Sletter Testgruppe")
    void deleteTestgruppe() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is(format("Finner ikke gruppe basert på gruppeID: %d", testgruppe.getId())));
    }

    @Test
    @DisplayName("Fjerner TestIdent fra Testgruppe")
    void deleteTestident() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        List<String> idents = testgruppe.getTestidenter().stream()
                .map(Testident::getIdent)
                .collect(Collectors.toList());

        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/slettTestident?ident=" + idents.get(0))
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        RsTestgruppeUtvidet resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, RsTestgruppeUtvidet.class);

        assertThat(resp.getTestidenter().size(), is(2));
    }

    @Test
    @DisplayName("Returnerer HTTP 404 Not Found når TestIdent ikke finnes")
    void shouldFail404WhenTestidentDontExist() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId() + "/slettTestident?ident=567")
                .andExpect(HttpStatus.NOT_FOUND, LinkedHashMap.class);
        assertThat(getErrMsg(resp), is("Testperson med ident 567 ble ikke funnet."));
    }

}
