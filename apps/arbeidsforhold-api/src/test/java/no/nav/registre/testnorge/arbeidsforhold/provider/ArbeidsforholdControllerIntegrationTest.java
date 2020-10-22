package no.nav.registre.testnorge.arbeidsforhold.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;

import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.AnsettelsesperiodeDTO;
import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsavtaleDTO;
import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsgiverDTO;
import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidstakerDTO;
import no.nav.registre.testnorge.arbeidsforhold.consumer.dto.PeriodeDTO;
import no.nav.registre.testnorge.libs.dto.arbeidsforhold.v1.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;

import org.junit.Before;
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

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureMockMvc(addFilters = false)
public class ArbeidsforholdControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String orgnummer = "123456789";
    private static final String personIdent = "12345678910";
    private static final String arbeidsforholdId = "1";
    private static final String yrke = "Tester";
    private static final float stillingsprosent = 100.00F;
    private static final String aaregUrl = "(.*)/aareg-test/api/v1/arbeidstaker/arbeidsforhold";
    private static final String tokenUrl = "(.*)/token-provider";

    private no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO[] aaregResponse;

    @Before
    public void setUp() {
        var arbeidsforhold = no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO.builder()
                .arbeidsforholdId(arbeidsforholdId)
                .arbeidsgiver(ArbeidsgiverDTO.builder().organisasjonsnummer(orgnummer).build())
                .arbeidsavtaler(Collections.singletonList(ArbeidsavtaleDTO.builder()
                        .stillingsprosent(stillingsprosent)
                        .yrke(yrke)
                        .build()))
                .ansettelsesperiode(AnsettelsesperiodeDTO.builder()
                        .periode(PeriodeDTO.builder()
                                .fom(LocalDate.now().minusMonths(3))
                                .tom(LocalDate.now())
                                .build())
                        .build())
                .arbeidstaker(ArbeidstakerDTO.builder()
                        .offentligIdent(personIdent)
                        .build())
                .build();

        aaregResponse = new no.nav.registre.testnorge.arbeidsforhold.consumer.dto.ArbeidsforholdDTO[] { arbeidsforhold };
    }

    @Test
    public void shouldGetArbeidsforhold() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenUrl)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(aaregUrl)
                .withResponseBody(aaregResponse)
                .stubGet();

        var mvcResultat = mockMvc.perform(get("/api/v1/arbeidsforhold/" + personIdent)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenUrl)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(aaregUrl)
                .withResponseBody(Collections.singletonList(aaregResponse))
                .verifyGet();

        var resultat = objectMapper.readValue(mvcResultat,
                ArbeidsforholdDTO[].class);

        assertThat(resultat).hasSize(1);
        assertThat(resultat[0]).isEqualToComparingFieldByField(new Arbeidsforhold(aaregResponse[0]));
    }

    @AfterEach
    public void cleanUp() {
        reset();
    }
}