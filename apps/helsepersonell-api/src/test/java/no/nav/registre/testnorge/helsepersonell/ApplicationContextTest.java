package no.nav.registre.testnorge.helsepersonell;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock
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
