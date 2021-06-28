package no.nav.testnav.proxies.safproxy;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = no.nav.testnav.safproxy.SafProxyApplicationStarter.class
)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApplicationContextTest {

    /**
     * Used to test app runtime dependency and properties
     */
    @Test
    @SuppressWarnings("java:S2699")
    public void load_app_context() {
    }
}
