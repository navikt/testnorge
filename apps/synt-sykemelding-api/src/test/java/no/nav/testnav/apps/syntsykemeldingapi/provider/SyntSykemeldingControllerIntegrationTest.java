package no.nav.testnav.apps.syntsykemeldingapi.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.*;
import no.nav.testnav.apps.syntsykemeldingapi.domain.HelsepersonellListe;
import no.nav.dolly.libs.nais.DollySpringBootTest;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
import no.nav.testnav.libs.dto.synt.sykemelding.v1.SyntSykemeldingDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Map;

import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureWireMock(port = 0)
class SyntSykemeldingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;

    @MockitoBean
    @SuppressWarnings("unused")
    private PdlProxyConsumer pdlProxyConsumer;

    @MockitoBean
    @SuppressWarnings("unused")
    private ArbeidsforholdConsumer arbeidsforholdConsumer;

    @MockitoBean
    @SuppressWarnings("unused")
    private OrganisasjonConsumer organisasjonConsumer;

    @MockitoBean
    @SuppressWarnings("unused")
    private HelsepersonellConsumer helsepersonellConsumer;

    @MockitoBean
    @SuppressWarnings("unused")
    private SykemeldingConsumer sykemeldingConsumer;

    @BeforeEach
    void beforeEach() {
        when(tokenExchange.exchange(any(ServerProperties.class)))
                .thenReturn(Mono.just(new AccessToken()));
    }

    @Test
    void shouldOpprettSyntSykemelding() throws Exception {

        var arbeidsforholdId = "1";
        var ident = "12345678910";
        var orgnummer = "123456789";

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/synt/api/v1/generate_sykmeldings_history_json")
                .withRequestBody(Map.of(ident, LocalDate.now().toString()))
                .withResponseBody(getTestHistorikk(ident))
                .stubPost(HttpStatus.OK);

        var request = SyntSykemeldingDTO
                .builder()
                .arbeidsforholdId(arbeidsforholdId)
                .ident(ident)
                .orgnummer(orgnummer)
                .startDato(LocalDate.now())
                .build();

        when(pdlProxyConsumer.getPdlPerson(anyString()))
                .thenReturn(getTestPdlPerson(ident));

        when(arbeidsforholdConsumer.getArbeidsforhold(anyString(), anyString(), anyString()))
                .thenReturn(getTestArbeidsforholdDTO(arbeidsforholdId, orgnummer));

        when(organisasjonConsumer.getOrganisasjon(anyString()))
                .thenReturn(getTestOrganisasjonDTO(orgnummer));

        when(helsepersonellConsumer.hentHelsepersonell())
                .thenReturn(new HelsepersonellListe(getTestLegeListeDTO()));

        mockMvc
                .perform(
                        post("/api/v1/synt-sykemelding")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(pdlProxyConsumer).getPdlPerson(anyString());
        verify(arbeidsforholdConsumer).getArbeidsforhold(anyString(), anyString(), anyString());
        verify(organisasjonConsumer).getOrganisasjon(anyString());
        verify(sykemeldingConsumer).opprettSykemelding(any(SykemeldingDTO.class));

    }

}