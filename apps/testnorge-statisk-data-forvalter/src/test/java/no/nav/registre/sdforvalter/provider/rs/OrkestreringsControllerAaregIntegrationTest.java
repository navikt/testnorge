package no.nav.registre.sdforvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static no.nav.registre.sdforvalter.ResourceUtils.getResourceFileContent;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sdforvalter.consumer.rs.aareg.request.RsAaregSyntetiseringsRequest;
import no.nav.registre.sdforvalter.consumer.rs.kodeverk.response.KodeverkResponse;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.core.type.TypeReference;


import java.util.Collections;
import java.util.List;

import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.testnav.libs.testing.JsonWiremockHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
class OrkestreringsControllerAaregIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AaregRepository aaregRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String FNR = "01010101010";
    private static final String ORGNR = "999999999";
    private static final String MILJOE = "test";

    private final String tokenResponse = "{\"access_token\": \"dummy\"}";
    private final KodeverkResponse kodeverkResponse = new KodeverkResponse(Collections.singletonList("yrke"));
    private static String syntString;
    private TypeReference<List<RsAaregSyntetiseringsRequest>> SYNT_RESPONSE = new TypeReference<>() {
    };

    @BeforeAll
    public static void setup() {
        syntString = getResourceFileContent("files/enkel_arbeidsforholdmelding.json");
    }

    @Test
    void shouldInitiateAaregFromDatabase() throws Exception {
        final AaregModel aaregModel = createAaregModel(FNR, ORGNR);
        aaregRepository.save(aaregModel);

        var arbeidsforholdmelding = objectMapper.readValue(syntString, SYNT_RESPONSE);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(Collections.emptyList())
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/synt-aareg/api/v1/generate_aareg")
                .withRequestBody(Collections.singletonList(FNR))
                .withResponseBody(arbeidsforholdmelding)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/kodeverk/api/v1/kodeverk/Yrker/koder")
                .withResponseBody(kodeverkResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidsforhold")
                .withResponseBody(Arbeidsforhold.builder().build())
                .stubPost();

        mvc.perform(post("/api/v1/orkestrering/aareg/" + MILJOE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(Collections.emptyList())
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/synt-aareg/api/v1/generate_aareg")
                .withRequestBody(Collections.singletonList(FNR))
                .withResponseBody(arbeidsforholdmelding)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/kodeverk/api/v1/kodeverk/Yrker/koder")
                .withResponseBody(kodeverkResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidsforhold")
                .withResponseBody(Arbeidsforhold.builder().build())
                .verifyPost();

    }

    @Test
    void shouldNotOppretteAaregWhenAlreadyExists() throws Exception {
        final AaregModel aaregModel = createAaregModel(FNR, ORGNR);
        aaregRepository.save(aaregModel);

        var arbeidsforholdmelding = objectMapper.readValue(syntString, SYNT_RESPONSE);
        var arbeidsforholdResponse = Collections.singletonList(arbeidsforholdmelding.get(0).getArbeidsforhold().toArbeidsforhold());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(arbeidsforholdResponse)
                .stubGet();

        mvc.perform(post("/api/v1/orkestrering/aareg/" + MILJOE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(arbeidsforholdResponse)
                .verifyGet();

    }

    @Test
    void shouldNotOppretteAaregIfSyntError() throws Exception {
        final AaregModel aaregModel = createAaregModel(FNR, ORGNR);
        aaregRepository.save(aaregModel);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(Collections.emptyList())
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/synt-aareg/api/v1/generate_aareg")
                .withRequestBody(Collections.singletonList(FNR))
                .withResponseBody("error")
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/kodeverk/api/v1/kodeverk/Yrker/koder")
                .withResponseBody(kodeverkResponse)
                .stubGet();

        mvc.perform(post("/api/v1/orkestrering/aareg/" + MILJOE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/token/oauth2/v2.0/token")
                .withResponseBody(tokenResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/aareg/test/api/v1/arbeidstaker/arbeidsforhold")
                .withResponseBody(Collections.emptyList())
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/synt-aareg/api/v1/generate_aareg")
                .withRequestBody(Collections.singletonList(FNR))
                .withResponseBody("error")
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/kodeverk/api/v1/kodeverk/Yrker/koder")
                .withResponseBody(kodeverkResponse)
                .verifyGet();
    }

    private AaregModel createAaregModel(String fnr, String orgId) {
        AaregModel model = new AaregModel();
        model.setFnr(fnr);
        model.setOrgId(orgId);
        return model;
    }

    @AfterEach
    public void cleanUp() {
        reset();
        aaregRepository.deleteAll();
    }

}