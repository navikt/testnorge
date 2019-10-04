package no.nav.dolly.provider.api.testgruppe;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.resultset.RsBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.RsOpprettEndreTestgruppe;
import no.nav.dolly.domain.resultset.RsTeamMedIdOgNavn;
import no.nav.dolly.domain.resultset.RsTestgruppeUtvidet;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@DisplayName("POST /api/v1/gruppe")
class TestgruppeControllerPostTest extends TestgruppeTestBase {

    @Test
    @DisplayName("Returnerer opprettet Testgruppe med innlogget bruker som eier")
    void createTestgruppeAndSetCurrentUserAsOwner() {
        Team team = dataFactory.createTeam("Teamnavn");

        RsOpprettEndreTestgruppe rsOpprettEndreTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .teamId(team.getId())
                .build();

        RsTestgruppeUtvidet resp = sendRequest(rsOpprettEndreTestgruppe)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.CREATED, RsTestgruppeUtvidet.class);

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
        assertThat(resp.getOpprettetAvNavIdent(), is("NAVIDENT"));
    }

    @Test
    @DisplayName("Returnerer opprettet Testgruppe med automatisk opprettet tilknyttet Team")
    void createTestgruppeWithoutSpecifyingTeam() {
        dataFactory.createBruker("NAVIDENT");

        RsOpprettEndreTestgruppe rsOpprettTestgruppe = RsOpprettEndreTestgruppe.builder()
                .navn("mingruppe")
                .hensikt("hensikt")
                .build();

        RsTestgruppeUtvidet resp = sendRequest(rsOpprettTestgruppe)
                .to(HttpMethod.POST, ENDPOINT_BASE_URI)
                .andExpect(HttpStatus.CREATED, RsTestgruppeUtvidet.class);

        RsTeamMedIdOgNavn team = resp.getTeam();

        assertThat(resp.getId(), is(notNullValue()));
        assertThat(resp.getNavn(), is("mingruppe"));
        assertThat(resp.getHensikt(), is("hensikt"));
        assertThat(team.getNavn(), is("NAVIDENT"));
    }

    @Test
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

        RsBestilling resp = sendRequest(rsDollyBestillingRequest)
                .to(HttpMethod.POST, url)
                .andExpect(HttpStatus.CREATED, RsBestilling.class);

        assertNotNull(resp.getTpsfKriterier());
    }
}
