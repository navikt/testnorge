package no.nav.registre.sdforvalter.provider.rs;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.sdforvalter.TestSecurityConfig;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsAaregSyntetiseringsRequest;
import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static no.nav.registre.sdforvalter.ResourceUtils.getResourceFileContent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@Import(TestSecurityConfig.class)
class OrkestreringControllerAaregIntegrationTest {

    private static final String FNR = "01010101010";
    private static final String ORGNR = "999999999";
    private static final String MILJOE = "test";
    private static String syntString;
    private final TypeReference<List<RsAaregSyntetiseringsRequest>> syntResponse = new TypeReference<>() {
    };

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;

    @Autowired
    private AaregRepository aaregRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private AaregModel createAaregModel() {
        AaregModel model = new AaregModel();
        model.setFnr(FNR);
        model.setOrgId(ORGNR);
        return model;
    }

    @AfterEach
    void cleanUp() {
        reset();
        aaregRepository.deleteAll();
    }

    @BeforeAll
    static void setup() {
        syntString = getResourceFileContent("files/enkel_arbeidsforholdmelding.json");
    }

    @Test
    void shouldNotOppretteAaregWhenAlreadyExists() throws Exception {
        final AaregModel aaregModel = createAaregModel();
        aaregRepository.save(aaregModel);

        var arbeidsforholdmelding = objectMapper.readValue(syntString, syntResponse);
        var arbeidsforholdResponse = Collections.singletonList(arbeidsforholdmelding.getFirst().getArbeidsforhold().toArbeidsforhold());

        when(tokenExchange.exchange(any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("dummy")));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(arbeidsforholdResponse)
                .stubGet();

        webTestClient
                .post()
                .uri("/api/v1/orkestrering/aareg/" + MILJOE)
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(arbeidsforholdResponse)
                .verifyGet();

    }

}
