package no.nav.dolly.provider.api.testgruppe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.LinkedHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;

@DisplayName("PUT /api/v1/gruppe")
class TestgruppeControllerPutTest extends TestgruppeTestBase {

    @Test
    @DisplayName("Returnerer HTTP 200 med feilmelding Not Found i body når Testgruppe ikke finnes")
    void shouldFail404WhenTestgruppeDontExist() {

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        LinkedHashMap resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Gruppe med id 123 ble ikke funnet."));
    }

    @Test
    @DisplayName("Oppdaterer informasjon om Testgruppe")
    void updateTestgruppe() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        RsTestgruppeMedBestillingId resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.PUT, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, RsTestgruppeMedBestillingId.class);

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
    }

}
