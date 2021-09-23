package no.nav.testnav.apps.endringsmeldingfrontend;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApplicationContextTest {

    @MockBean
    public JwtDecoder jwtDecoder;
    /**
     * Used to test app runtime dependency and properties
     */
    @Test
    @SuppressWarnings("java:S2699")
    public void load_app_context() {
    }
}
