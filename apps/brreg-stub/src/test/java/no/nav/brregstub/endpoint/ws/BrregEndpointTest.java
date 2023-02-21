package no.nav.brregstub.endpoint.ws;

import no.nav.brregstub.tjenestekontrakter.ws.ErFr;
import no.nav.common.cxf.CXFClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
public class BrregEndpointTest {

    @Value("${server.port}")
    private int randomServerPort;

    private ErFr client;

    @Test
    @Disabled
    @DisplayName("Soap-klient kaller hentRolleutskrift-endepunktet til brreg-stub")
    public void skalKalleHentRolleutskriftEndpoint() {
        var rolleUtskrift = client.hentRolleutskrift("bruker", "pwd", "123");
        assertThat(rolleUtskrift).contains("tjeneste=\"hentRolleutskrift\">");
        assertThat(rolleUtskrift).contains("<fodselsnr>123</fodselsnr>");
    }

    @Test
    @Disabled
    @DisplayName("Soap-klient kaller hentRoller-endepunktet til brreg-stub")
    public void skalKalleHentRolleEndpoint() {
        var rolleUtskrift = client.hentRoller("bruker", "pwd", "321");
        assertThat(rolleUtskrift).contains("tjeneste=\"hentRoller\">");
        assertThat(rolleUtskrift).contains("<orgnr>321</orgnr>");
    }

    @BeforeAll
    void onSetup() {
        client = (ErFr) new CXFClient(ErFr.class)
                .address("http://localhost:" + randomServerPort + "/ws")
                .build();
    }
}
