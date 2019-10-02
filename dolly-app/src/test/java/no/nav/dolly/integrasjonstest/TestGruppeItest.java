package no.nav.dolly.integrasjonstest;

import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class TestGruppeItest extends ITestBase {

    private static final String TESTGRUPPE_URI = "/api/v1/gruppe";

    @Test
    @DisplayName("Oppretter en grunnleggende bestilling")
    void opprettGruppe() throws Exception {
        String requestBody = getJsonContentsAsString("opprettTestgruppe.json");
        postGruppe(requestBody);

        // todo her skal vi hente opp hva som ble lagret fra databasen

        // så skal vi sjekke at innholdet på det vi har lagret er fornuftig

    }

    private ResponseEntity<RsTestgruppeUtvidet> postGruppe(String body) {
        return this.restTemplate.exchange(TESTGRUPPE_URI, HttpMethod.POST, createHttpEntityWithBody(body), RsTestgruppeUtvidet.class);
    }

    private HttpEntity createHttpEntityWithBody(String body) {
        return new HttpEntity(body, createHeaders());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }
}
