package no.nav.brregstub.endpoint.ws;

import no.nav.brregstub.tjenestekontrakter.ws.ErFr;
import no.nav.common.cxf.CXFClient;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@DollySpringBootTest
@TestInstance(PER_CLASS)
class BrregEndpointTest {

    @LocalServerPort
    private int port;

    private ErFr client;

    @BeforeAll
    void beforeAll() {
        client = new CXFClient<>(ErFr.class)
                .address("http://localhost:%d/ws".formatted(port))
                .build();
    }

    @Test
    @DisplayName("Soap-klient kaller hentRolleutskrift-endepunktet til brreg-stub")
    void skalKalleHentRolleutskriftEndpoint() {
        var rolleUtskrift = client.hentRolleutskrift("bruker", "pwd", "123");
        assertThat(rolleUtskrift)
                .contains("tjeneste=\"hentRolleutskrift\">")
                .contains("<fodselsnr>123</fodselsnr>");
    }

    @Test
    @DisplayName("Soap-klient kaller hentRoller-endepunktet til brreg-stub")
    void skalKalleHentRolleEndpoint() {
        var rolleUtskrift = client.hentRoller("bruker", "pwd", "321");
        assertThat(rolleUtskrift)
                .contains("tjeneste=\"hentRoller\">")
                .contains("<orgnr>321</orgnr>");
    }

}
