package no.nav.registre.hodejegeren;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTestBase {
    
    @ClassRule
    public static WireMockClassRule identpoolStatic = new WireMockClassRule(options().dynamicPort());
    @ClassRule
    public static WireMockClassRule tpsfStatic = new WireMockClassRule(options().dynamicPort());
    @ClassRule
    public static WireMockClassRule tpsSyntStatic = new WireMockClassRule(options().dynamicPort());
    
    @BeforeClass
    public static void beforeClass() throws Exception {
        // Set consumed REST-APIs' properties:
        System.setProperty("ident-pool.rest-api.url", "http://localhost:" + identpoolStatic.port() + "/api");
        System.setProperty("tps-forvalteren.rest-api.url", "http://localhost:" + tpsfStatic.port() + "/api");
        System.setProperty("tps-syntetisereren.rest-api.url", "http://localhost:" + tpsSyntStatic.port() + "/api");
        System.setProperty("hodejegeren.ida.credential.username", "ida-username");
        System.setProperty("hodejegeren.ida.credential.password", "ida-password");
    }
}
