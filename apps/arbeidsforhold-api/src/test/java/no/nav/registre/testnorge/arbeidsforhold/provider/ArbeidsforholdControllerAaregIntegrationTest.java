package no.nav.registre.testnorge.arbeidsforhold.provider;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsavtaleDTO;
import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsgiverDTO;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ArbeidsforholdControllerAaregIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String orgnummer = "123456789";
    private static final String personIdent = "12345678910";
    private static final String arbeidsforholdId = "1";

    @Test
    public void shouldGetArbeidsforhold() throws Exception {

        var aaregUrl = "(.*)/aareg-test/api/v1/arbeidstaker/arbeidsforhold";
        var tokenUrl =  "(.*)/token-provider";

        var aaregResponse = ArbeidsforholdDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .arbeidsgiver(ArbeidsgiverDTO.builder().organisasjonsnummer(orgnummer).build())
                .arbeidsavtaler(Collections.singletonList(ArbeidsavtaleDTO.builder()
                        .stillingsprosent(100.00)
                        .yrke("Test yrke")
                        .build()))
                .build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenUrl)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(aaregUrl)
                .withResponseBody(Collections.singletonList(aaregResponse))
                .stubGet();

        mockMvc.perform(get("/api/v1/arbeidsforhold/" + personIdent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(aaregUrl)
                .withResponseBody(Collections.singletonList(aaregResponse))
                .verifyGet();
    }

    @AfterEach
    public void cleanUp() {
        reset();
    }
}