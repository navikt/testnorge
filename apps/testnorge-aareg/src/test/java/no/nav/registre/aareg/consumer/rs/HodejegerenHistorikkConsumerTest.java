package no.nav.registre.aareg.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import no.nav.registre.aareg.AaregSaveInHodejegerenRequest;
import no.nav.registre.aareg.IdentMedData;
import no.nav.registre.aareg.domain.RsArbeidsavtale;
import no.nav.registre.aareg.domain.RsArbeidsforhold;
import no.nav.registre.aareg.domain.RsOrganisasjon;
import no.nav.registre.aareg.domain.RsPeriode;
import no.nav.registre.aareg.domain.RsPersonAareg;

@ExtendWith(MockitoExtension.class)
@RestClientTest(HodejegerenHistorikkConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
class HodejegerenHistorikkConsumerTest {

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-hodejegeren.rest-api.url}")
    private String serverUrl;

    private AaregSaveInHodejegerenRequest aaregSaveInHodejegerenRequest;
    private List<IdentMedData> identMedData;
    private List<RsArbeidsforhold> data;
    private String fnr = "01010101010";

    @BeforeEach
    public void setUp() {
        data = new ArrayList<>(Collections.singletonList(RsArbeidsforhold.builder()
                .ansettelsesPeriode(RsPeriode.builder()
                        .fom(LocalDateTime.of(2016, 2, 1, 0, 0, 0))
                        .build())
                .arbeidsavtale(RsArbeidsavtale.builder()
                        .arbeidstidsordning("ikkeSkift")
                        .avtaltArbeidstimerPerUke(36.5)
                        .stillingsprosent(100.0)
                        .endringsdatoStillingsprosent(LocalDateTime.of(2016, 2, 1, 0, 0, 0))
                        .yrke("8281101")
                        .build())
                .arbeidsforholdID("NMN1dglP5FQ7iu4e")
                .arbeidsforholdstype("ordinaertArbeidsforhold")
                .arbeidsgiver(RsOrganisasjon.builder()
                        .orgnummer("974722992")
                        .build())
                .arbeidstaker(RsPersonAareg.builder()
                        .ident(fnr)
                        .identtype("FNR")
                        .build())
                .build()));

        identMedData = new ArrayList<>(Collections.singletonList(IdentMedData.builder()
                .id(fnr)
                .data(data)
                .build()));

        aaregSaveInHodejegerenRequest = AaregSaveInHodejegerenRequest.builder()
                .kilde("aareg")
                .identMedData(identMedData)
                .build();
    }

    @Test
    void shouldSendHistorikkToHodejegeren() throws JsonProcessingException {
        var expectedUri = serverUrl + "/v1/historikk/";
        stubSaveInHodejegeren(expectedUri);

        var response = hodejegerenHistorikkConsumer.saveHistory(aaregSaveInHodejegerenRequest);

        assertThat(response.get(0), equalTo(fnr));
    }

    private void stubSaveInHodejegeren(String expectedUri) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(asJsonString(aaregSaveInHodejegerenRequest)))
                .andRespond(withSuccess("[\"" + fnr + "\"]", MediaType.APPLICATION_JSON));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}