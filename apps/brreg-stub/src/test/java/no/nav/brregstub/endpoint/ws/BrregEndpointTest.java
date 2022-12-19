package no.nav.brregstub.endpoint.ws;

import no.nav.brregstub.tjenestekontrakter.ws.ErFr;
import no.nav.common.cxf.CXFClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
class BrregEndpointTest {

    @LocalServerPort
    private int randomServerPort;

    private ErFr client;

    @BeforeAll
    void onSetup() {
        client = new CXFClient<>(ErFr.class)
                .address("http://localhost:" + randomServerPort + "/ws")
                .build();
    }

    @Test
    @Disabled("Hvorfor er denne disabled?") // TODO: Hvorfor er denne disabled?
    @DisplayName("Soap-klient kaller hentRolleutskrift-endepunktet til brreg-stub")
    void skalKalleHentRolleutskriftEndpoint() {
        var rolleUtskrift = client.hentRolleutskrift("bruker", "pwd", "123");
        assertThat(rolleUtskrift)
                .contains("tjeneste=\"hentRolleutskrift\">")
                .contains("<fodselsnr>123</fodselsnr>");
    }

    @Test
    @Disabled("Hvorfor er denne disabled?") // TODO: Hvorfor er denne disabled?
    @DisplayName("Soap-klient kaller hentRoller-endepunktet til brreg-stub")
    void skalKalleHentRolleEndpoint() {
        var rolleUtskrift = client.hentRoller("bruker", "pwd", "321");
        assertThat(rolleUtskrift)
                .contains("tjeneste=\"hentRoller\">")
                .contains("<orgnr>321</orgnr>");
    }
}
