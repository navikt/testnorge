package no.nav.registre.testnorge.organisasjonservice.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.DetaljerDTO;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.NavnDTO;
import no.nav.registre.testnorge.organisasjonservice.consumer.dto.OrganisasjonDTO;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureWebTestClient
class OrganisasjonControllerEregIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ORGNUMMER = "123456789";
    private static final String MILJO = "test";
    private static final String EREG_URL = "/api/{miljo}/v1/organisasjon/" + ORGNUMMER;

    private OrganisasjonDTO organisasjonReponse;

    @BeforeEach
    void setUp() {
        organisasjonReponse = OrganisasjonDTO.builder()
                .navn(NavnDTO.builder().navnelinje1("NavneLinje").redigertnavn("RedigertNavn").build())
                .type("Type")
                .detaljer(DetaljerDTO.builder().enhetstype("Enhetstype").build())
                .organisasjonsnummer(ORGNUMMER)
                .build();
    }

    @Test
    @Disabled("Not sure why this is disabled; this class works like an simple application context now")
    void shouldGetOrganisasjon() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(EREG_URL)
                .withQueryParam("inkluderHierarki", "true")
                .withQueryParam("inkluderHistorikk", "false")
                .withResponseBody(organisasjonReponse)
                .stubGet();


        webTestClient
                .mutateWith(mockJwt())
                .get()
                .uri("/api/v1/organisasjoner/" + ORGNUMMER)
                .header("miljo", MILJO)
                .exchange();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(EREG_URL)
                .withQueryParam("inkluderHierarki", "true")
                .withQueryParam("inkluderHistorikk", "false")
                .withResponseBody(organisasjonReponse)
                .verifyGet();

    }

    @AfterEach
    void tearDown() {
        reset();
    }

}