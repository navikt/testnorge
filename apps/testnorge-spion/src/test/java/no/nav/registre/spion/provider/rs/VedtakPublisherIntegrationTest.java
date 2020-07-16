package no.nav.registre.spion.provider.rs;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@DirtiesContext
@TestPropertySource(locations = "classpath:application-test.properties")
class VedtakPublisherIntegrationTest {

    @AfterEach
    void tearDown() {
        WireMock.reset();
    }
}