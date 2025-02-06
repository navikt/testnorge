package no.nav.testnav.apps.syntsykemeldingapi.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.libs.nais.DollySpringBootTest;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.testnav.apps.syntsykemeldingapi.util.TestUtil.getTestOrganisasjonDTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class OrganisasjonConsumerTest {

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrganisasjonConsumer organisasjonConsumer;

    private static final String ORGNR = "123456789";
    private static final String ORGANISASJON_URL = "(.*)/organisasjon/api/v1/organisasjoner/" + ORGNR;
    private OrganisasjonDTO organisasjonResponse;

    @BeforeEach
    void before() {
        WireMock.reset();
        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
        organisasjonResponse = getTestOrganisasjonDTO(ORGNR);
    }

    @Test
    void shouldGetOrgansiasjon() throws JsonProcessingException {
        stubOrgansisasjon();

        var response = organisasjonConsumer.getOrganisasjon(ORGNR);

        assertThat(response).isNotNull().isEqualTo(organisasjonResponse);
    }

    private void stubOrgansisasjon() throws JsonProcessingException {
        stubFor(get(urlPathMatching(ORGANISASJON_URL))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(organisasjonResponse))));
    }

}
