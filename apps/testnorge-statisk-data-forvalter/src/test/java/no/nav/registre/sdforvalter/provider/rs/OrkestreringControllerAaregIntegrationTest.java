package no.nav.registre.sdforvalter.provider.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsAaregSyntetiseringsRequest;
import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static no.nav.registre.sdforvalter.ResourceUtils.getResourceFileContent;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class OrkestreringControllerAaregIntegrationTest {

    private static final String FNR = "01010101010";
    private static final String ORGNR = "999999999";
    private static final String MILJOE = "test";
    private static String syntString;
    private final TypeReference<List<RsAaregSyntetiseringsRequest>> syntResponse = new TypeReference<>() {
    };

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    @SuppressWarnings("unused")
    private TokenExchange tokenExchange;

    @Autowired
    private AaregRepository aaregRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanUp() {
        reset();
        aaregRepository.deleteAll();
    }

    @BeforeAll
    static void setup() {
        syntString = getResourceFileContent("files/enkel_arbeidsforholdmelding.json");
    }

    private AaregModel createAaregModel() {
        AaregModel model = new AaregModel();
        model.setFnr(FNR);
        model.setOrgId(ORGNR);
        return model;
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

        mvc.perform(post("/api/v1/orkestrering/aareg/" + MILJOE)
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(arbeidsforholdResponse)
                .verifyGet();

    }

}
