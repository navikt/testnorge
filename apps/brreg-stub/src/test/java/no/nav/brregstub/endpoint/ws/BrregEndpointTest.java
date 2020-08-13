package no.nav.brregstub.endpoint.ws;

import no.nav.brregstub.ApplicationConfig;
import no.nav.brregstub.tjenestekontrakter.ws.ErFr;
import no.nav.sbl.dialogarena.common.cxf.CXFClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ApplicationConfig.class})
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BrregEndpointTest {

    @LocalServerPort
    private int randomServerPort;

    private ErFr client;

    @BeforeAll
    void onSetup() {
        client = (ErFr) new CXFClient(ErFr.class)
                .address("http://localhost:" + randomServerPort + "/ws")
                .build();
    }

    @Test
    @DisplayName("Soap-klient kaller hentRolleutskrift-endepunktet til brreg-stub")
    public void skalKalleHentRolleutskriftEndpoint() {
        var rolleUtskrift = client.hentRolleutskrift("bruker", "pwd", "123");
        assertThat(rolleUtskrift).contains("tjeneste=\"hentRolleutskrift\">");
        assertThat(rolleUtskrift).contains("<fodselsnr>123</fodselsnr>");
    }

    @Test
    @DisplayName("Soap-klient kaller hentRoller-endepunktet til brreg-stub")
    public void skalKalleHentRolleEndpoint() {
        var rolleUtskrift = client.hentRoller("bruker", "pwd", "321");
        assertThat(rolleUtskrift).contains("tjeneste=\"hentRoller\">");
        assertThat(rolleUtskrift).contains("<orgnr>321</orgnr>");
    }
}
