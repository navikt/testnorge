package no.nav.registre.sdforvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.request.SendToTpsRequest;
import no.nav.registre.sdforvalter.consumer.rs.tpsf.response.SkdMeldingerTilTpsRespons;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.registre.sdforvalter.database.repository.TpsIdenterRepository;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class OrkestreringsControllerIdentIntegrationTest {

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

    private static final String ENVIRONMENT = "t1";
    private static final String FNR = "01010101011";

    @Autowired
    private MockMvc mvc;

    @MockBean
    public JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TpsIdenterRepository tpsIdenterRepository;

    private String hodejegerenUrlPattern;
    private String levendeIdenterUrlPattern;
    private String tpsfGetMeldingerUrlPattern;
    private String tpsfSendSkdmeldingerUrlPattern;
    private String tpOpprettPersonerUrlPattern;
    private List<String> identer = Collections.singletonList(FNR);
    private List<Long> skdMeldinger = Collections.singletonList(1234567L);
    private final String tokenResponse = "{\"access_token\": \"dummy\"}";

    @BeforeEach
    public void setup() {
        hodejegerenUrlPattern = "(.*)/hodejegeren/api/v1/alle-identer/" + staticDataPlaygroup;
        levendeIdenterUrlPattern = "(.*)/hodejegeren/api/v1/levende-identer/" + staticDataPlaygroup;
        tpsfGetMeldingerUrlPattern = "(.*)/tpsf/api/v1/endringsmelding/skd/meldinger/" + staticDataPlaygroup;
        tpsfSendSkdmeldingerUrlPattern = "(.*)/tpsf/api/v1/endringsmelding/skd/send/" + staticDataPlaygroup;
        tpOpprettPersonerUrlPattern = "(.*)/testnorge-tp/api/v1/orkestrering/opprettPersoner/" + ENVIRONMENT;

        JsonWiremockHelper.builder(objectMapper).withUrlPathMatching("(.*)/v1/orkestrering/opprettPersoner/(.*)").stubPost();
    }


    @Test
    void shouldInitiateIdent() throws Exception {
        final TpsIdentModel tpsIdent = create(FNR, "Test", "Testen");
        tpsIdenterRepository.save(tpsIdent);

        var sendToTpsfRequest = new SendToTpsRequest(ENVIRONMENT, skdMeldinger);
        var skdResponse = SkdMeldingerTilTpsRespons.builder()
                .antallSendte(1)
                .antallFeilet(0)
                .build();

        when(tokenExchange.exchange(any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("dummy")));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrlPattern)
                .withResponseBody(Collections.EMPTY_SET)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tpsfGetMeldingerUrlPattern)
                .withResponseBody(Collections.singletonList((1234567)))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tpsfSendSkdmeldingerUrlPattern)
                .withRequestBody(sendToTpsfRequest)
                .withResponseBody(skdResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(levendeIdenterUrlPattern)
                .withResponseBody(identer)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tpOpprettPersonerUrlPattern)
                .withRequestBody(identer)
                .withResponseBody(identer)
                .stubPost();

        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrlPattern)
                .withResponseBody(Collections.EMPTY_SET)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tpsfGetMeldingerUrlPattern)
                .withResponseBody(Collections.singletonList((1234567)))
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tpsfSendSkdmeldingerUrlPattern)
                .withRequestBody(sendToTpsfRequest)
                .withResponseBody(skdResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(levendeIdenterUrlPattern)
                .withResponseBody(identer)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tpOpprettPersonerUrlPattern)
                .withRequestBody(identer)
                .withResponseBody(identer)
                .verifyPost();
    }

    private TpsIdentModel create(String fnr, String firstName, String lastName) {
        TpsIdentModel model = new TpsIdentModel();
        model.setFnr(fnr);
        model.setFirstName(firstName);
        model.setLastName(lastName);
        return model;
    }

    @AfterEach
    public void cleanUp() {
        reset();
        tpsIdenterRepository.deleteAll();
    }


}
