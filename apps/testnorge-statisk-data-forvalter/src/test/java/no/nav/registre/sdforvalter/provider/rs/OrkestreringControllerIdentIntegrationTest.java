package no.nav.registre.sdforvalter.provider.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class OrkestreringControllerIdentIntegrationTest {

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private ObjectMapper objectMapper;

    private String hodejegerenUrlPattern;

    @BeforeEach
    public void setup() {
        hodejegerenUrlPattern = "(.*)/hodejegeren/api/v1/alle-identer/" + staticDataPlaygroup;

        JsonWiremockHelper.builder(objectMapper).withUrlPathMatching("(.*)/v1/orkestrering/opprettPersoner/(.*)").stubPost(HttpStatus.OK);
    }

    @Test
    void shouldInitiateIdent() throws Exception {

        when(tokenExchange.exchange(any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("dummy")));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrlPattern)
                .withResponseBody(Collections.EMPTY_SET)
                .stubGet();
    }
}
