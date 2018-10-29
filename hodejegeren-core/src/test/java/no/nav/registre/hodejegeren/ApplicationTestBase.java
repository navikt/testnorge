package no.nav.registre.hodejegeren;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class ApplicationTestBase {
    
    @BeforeClass
    public static void beforeClass() {
        // Set consumed REST-APIs' properties:
        System.setProperty("ident-pool.rest-api.url", "http://localhost:${wiremock.server.port}/identpool/api");
        System.setProperty("tps-forvalteren.rest-api.url", "http://localhost:${wiremock.server.port}/tpsf/api");
        System.setProperty("tps-syntetisereren.rest-api.url", "http://localhost:${wiremock.server.port}/tpssynt/api");
        System.setProperty("hodejegeren.ida.credential.username", "ida-username");
        System.setProperty("hodejegeren.ida.credential.password", "ida-password");
    }
}
