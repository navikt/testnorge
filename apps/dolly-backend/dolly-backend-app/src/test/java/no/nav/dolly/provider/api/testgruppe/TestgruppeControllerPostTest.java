package no.nav.dolly.provider.api.testgruppe;

import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingStatus;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.entity.testgruppe.RsTestgruppeMedBestillingId;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
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

import java.time.LocalDate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Disabled
@DisplayName("POST /api/v1/gruppe")
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class,
        OAuth2ClientAutoConfiguration.class,
        ManagementWebSecurityAutoConfiguration.class })
@AutoConfigureMockMvc(addFilters = false)
class TestgruppeControllerPostTest extends TestgruppeTestBase {

    @Test
    @DisplayName("Returnerer opprettet Testgruppe med innlogget bruker som eier")
    void createTestgruppeAndSetCurrentUserAsOwner() {

        dataFactory.createBruker("NAVIDENT");

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        RsTestgruppeMedBestillingId resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.CREATED, RsTestgruppeMedBestillingId.class);

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
        assertThat(resp.getOpprettetAv().getBrukerId(), is("NAVIDENT"));
    }

    @Test
    @Disabled
    @DisplayName("Oppretter TPS bestilling")
    void createTpsBestilling() {
        Long gruppeId = dataFactory.createTestgruppe("Test gruppe").getId();

        String url = ENDPOINT_BASE_URI + "/" + gruppeId + "/bestilling";

        RsTpsfUtvidetBestilling tpsfBestilling = RsTpsfUtvidetBestilling.builder()
                .kjonn("M")
                .foedtEtter(LocalDate.of(2000, 1, 1).atStartOfDay())
                .build();

        RsDollyBestillingRequest rsDollyBestillingRequest = new RsDollyBestillingRequest();

        rsDollyBestillingRequest.setTpsf(tpsfBestilling);

        RsBestillingStatus resp = sendRequest(rsDollyBestillingRequest)
                .to(HttpMethod.POST, url)
                .andExpect(HttpStatus.CREATED, RsBestillingStatus.class);

        assertNotNull(resp.getBestilling().getTpsf());
    }
}
