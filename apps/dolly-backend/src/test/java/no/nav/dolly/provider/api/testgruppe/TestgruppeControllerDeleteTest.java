package no.nav.dolly.provider.api.testgruppe;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DisplayName("DELETE /api/v1/gruppe")
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class })
@AutoConfigureMockMvc(addFilters = false)
public class TestgruppeControllerDeleteTest extends TestgruppeTestBase {

    @Test
    @Disabled
    @DisplayName("Returnerer HTTP 200 med feilmelding Not Found i body ved sletting når Testgruppe ikke finnes")
    public void shouldFail404WhenTestgruppeDontExist() {
        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/123")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is("Gruppe med id 123 ble ikke funnet."));
    }

    @Test
    @Disabled
    @DisplayName("Sletter Testgruppe")
    public void deleteTestgruppe() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        sendRequest()
                .to(HttpMethod.DELETE, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        assertThat(getErrMsg(resp), is(format("Gruppe med id %d ble ikke funnet.", testgruppe.getId())));
    }

    @Test
    @Disabled
    @DisplayName("Fjerner TestIdent fra Testgruppe")
    public void deleteTestident() {
        Testgruppe testgruppe = dataFactory.createTestgruppe("Testgruppe");

        List<String> idents = testgruppe.getTestidenter().stream()
                .map(Testident::getIdent)
                .collect(Collectors.toList());

        sendRequest()
                .to(HttpMethod.DELETE, "/api/v1/ident/" + idents.get(0))
                .andExpect(HttpStatus.OK, LinkedHashMap.class);

        RsTestgruppeMedBestillingId resp = sendRequest()
                .to(HttpMethod.GET, ENDPOINT_BASE_URI + "/" + testgruppe.getId())
                .andExpect(HttpStatus.OK, RsTestgruppeMedBestillingId.class);

        assertThat(resp.getIdenter().size(), is(2));
    }

    @Test
    @Disabled
    @DisplayName("Returnerer HTTP 200 med feilmelding Not Found i body når TestIdent ikke finnes")
    public void shouldFail404WhenTestidentDontExist() {

        dataFactory.createTestgruppe("Testgruppe");

        LinkedHashMap resp = sendRequest()
                .to(HttpMethod.DELETE, "/api/v1/ident/567")
                .andExpect(HttpStatus.OK, LinkedHashMap.class);
        assertThat(getErrMsg(resp), is("Testperson med ident 567 ble ikke funnet."));
    }

}
