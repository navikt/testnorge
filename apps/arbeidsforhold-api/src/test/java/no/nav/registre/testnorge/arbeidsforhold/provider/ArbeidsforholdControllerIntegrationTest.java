package no.nav.registre.testnorge.arbeidsforhold.provider;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

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
import com.github.tomakehurst.wiremock.client.WireMock;

import no.nav.registre.testnorge.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.dto.hodejegeren.v1.PersondataDTO;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ArbeidsforholdControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void should_get_single_arbeidsforhold() throws Exception {

        String personIdent = "12125678903";
        String orgnummer = "163852480";
        String arbeidsforholdId = "1";

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/persondata")
                .withQueryParam("ident", personIdent)
                .withResponseBody(PersondataDTO.builder()
                        .fnr(personIdent)
                        .fornavn("Synt")
                        .etternavn("Syntesen")
                        .build()
                ).stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/arbeidsforhold")
                .withQueryParam("personIdent", personIdent)
                .withQueryParam("orgnummer", orgnummer)
                .withQueryParam("arbeidsforholdId", arbeidsforholdId)
                .withResponseBody(ArbeidsforholdDTO.builder()
                        .arbeidsforholdId(arbeidsforholdId)
                        .orgnummer(orgnummer)
                        .stillingsprosent(100.0)
                        .yrke("Synt")
                        .build()
                ).stubGet();

        String json = mockMvc.perform(get("/api/v1/arbeidsforhold/")
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        //TODO fullføre test, fungerer ikke
    }

    @AfterEach
    public void cleanUp() {
        WireMock.reset();}
}