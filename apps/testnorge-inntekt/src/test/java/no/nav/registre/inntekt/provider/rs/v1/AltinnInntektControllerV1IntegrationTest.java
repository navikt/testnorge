package no.nav.registre.inntekt.provider.rs.v1;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums.YtelseKodeListe;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsArbeidsgiver;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsKontaktinformasjon;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotResponse;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.RsJoarkMetadata;
import no.nav.registre.inntekt.provider.rs.requests.AltinnInntektsmeldingRequest;
import no.nav.registre.inntekt.repository.InntektsmedlingRepository;
import no.nav.registre.inntekt.repository.model.InntektsmeldingModel;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AltinnInntektControllerV1IntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InntektsmedlingRepository repository;

    @Test
    void should_send_inntektsmelding() throws Exception {
        RsInntektsmeldingRequest im = RsInntektsmeldingRequest
                .builder()
                .arbeidsgiver(RsArbeidsgiver
                        .builder()
                        .virksomhetsnummer("98765432")
                        .kontaktinformasjon(RsKontaktinformasjon.builder().kontaktinformasjonNavn("Dolly Test Inc.").build())
                        .build()
                )
                .ytelse(YtelseKodeListe.FORELDREPENGER)
                .build();

        String ident = "10100101011";
        AltinnInntektsmeldingRequest request = new AltinnInntektsmeldingRequest("qx", ident, new RsJoarkMetadata(), Collections.singletonList(im));

        String content = "<dummy/>";
        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v2/inntektsmelding/2018/12/11")
                .withResponseBody(content)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/")
                .withResponseBody(Token.builder()
                        .access_token("access")
                        .expires_in(LocalDateTime.now().plusMinutes(60))
                        .build())
                .stubGet();

        JsonWiremockHelper.builder(objectMapper)
                .withUrlPathMatching("(.*)/rest/journalpostapi/v1/journalpost")
                .withResponseBody(new DokmotResponse())
                .stubPost();

        mvc.perform(
                post("/api/v1/altinnInntekt/enkeltident")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Nav-Call-Id", "test")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertThat(repository.findAll())
                .usingElementComparatorIgnoringFields("createdAt")
                .containsOnly(new InntektsmeldingModel(1L, ident, "\"" + content + "\"", null));

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/rest/journalpostapi/v1/journalpost")
                .verifyPost();

    }

    @Test
    void should_create_inntektsmelding_with_serial_id_in_db() throws Exception {
        RsInntektsmeldingRequest im = RsInntektsmeldingRequest
                .builder()
                .arbeidsgiver(RsArbeidsgiver
                        .builder()
                        .virksomhetsnummer("98765432")
                        .kontaktinformasjon(RsKontaktinformasjon.builder().kontaktinformasjonNavn("Dolly Test Inc.").build())
                        .build()
                )
                .ytelse(YtelseKodeListe.FORELDREPENGER)
                .build();

        String ident = "10100101011";
        AltinnInntektsmeldingRequest request = new AltinnInntektsmeldingRequest("qx", ident, new RsJoarkMetadata(), Arrays.asList(im, im, im, im));

        String content = "<dummy/>";
        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v2/inntektsmelding/2018/12/11")
                .withResponseBody(content)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/token/")
                .withResponseBody(Token.builder()
                        .access_token("access")
                        .expires_in(LocalDateTime.now().plusMinutes(60))
                        .build())
                .stubGet();

        JsonWiremockHelper.builder(objectMapper)
                .withUrlPathMatching("(.*)/rest/journalpostapi/v1/journalpost")
                .withResponseBody(new DokmotResponse())
                .stubPost();

        mvc.perform(
                post("/api/v1/altinnInntekt/enkeltident")
                        .content(objectMapper.writeValueAsString(request))
                        .header("Nav-Call-Id", "test")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/rest/journalpostapi/v1/journalpost")
                .verifyPost();

        assertThat(repository.findAll())
                .usingElementComparatorIgnoringFields("createdAt")
                .containsOnly(
                        new InntektsmeldingModel(1L, ident, "\"" + content + "\"", null),
                        new InntektsmeldingModel(2L, ident, "\"" + content + "\"", null),
                        new InntektsmeldingModel(3L, ident, "\"" + content + "\"", null),
                        new InntektsmeldingModel(4L, ident, "\"" + content + "\"", null)
                );

    }


    @AfterEach
    void cleanup() {
        reset();
        repository.deleteAll();
    }
}