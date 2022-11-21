package no.nav.registre.testnorge.generersyntameldingservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnorge.generersyntameldingservice.domain.ArbeidsforholdType;
import no.nav.registre.testnorge.generersyntameldingservice.provider.request.SyntAmeldingRequest;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import no.nav.testnav.libs.testing.JsonWiremockHelper;

import java.time.LocalDate;
import java.util.List;

import static no.nav.registre.testnorge.generersyntameldingservice.ResourceUtils.getResourceFileContent;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.yml")
@ActiveProfiles("test")
@Disabled
public class SyntControllerIntegrationTest {

    @MockBean
    public JwtDecoder jwtDecoder;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String tokenPath = "(.*)/token/oauth2/v2.0/token";
    private final String syntArbeidsforholdPath = "(.*)/synt/api/v1/arbeidsforhold/start/ordinaert";
    private final String syntHistorikkPath = "(.*)/synt/api/v1/arbeidsforhold";
    private final String tokenResponse = "{\"access_token\": \"dummy\"}";
    private static String arbeidsforholdResponse;
    private static String historikkResponse;

    private TypeReference<List<Arbeidsforhold>> HISTORIKK_RESPONSE = new TypeReference<>() {
    };

    private final SyntAmeldingRequest request = new SyntAmeldingRequest(
            ArbeidsforholdType.ordinaertArbeidsforhold,
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 7, 1));

    @BeforeAll
    public static void setup() {
        arbeidsforholdResponse = getResourceFileContent("files/synt_arbeidsforhold.json");
        historikkResponse = getResourceFileContent("files/synt_historikk.json");
    }

    @Test
    void shouldInitiateIdent() throws Exception {
        var arbeidsforhold = objectMapper.readValue(arbeidsforholdResponse, Arbeidsforhold.class);
        var historikk = objectMapper.readValue(historikkResponse, HISTORIKK_RESPONSE);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenPath)
                .withResponseBody(tokenResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(syntArbeidsforholdPath)
                .withResponseBody(arbeidsforhold)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(syntHistorikkPath)
                .withRequestBody(arbeidsforhold)
                .withResponseBody(historikk)
                .stubPost();


        mvc.perform(post("/api/v1/generer/amelding")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenPath)
                .withResponseBody(tokenResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(syntArbeidsforholdPath)
                .withResponseBody(arbeidsforhold)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(syntHistorikkPath)
                .withRequestBody(arbeidsforhold)
                .withResponseBody(historikk)
                .verifyPost();

    }
}
