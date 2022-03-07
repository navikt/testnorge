package no.nav.registre.inntekt.consumer.rs.v2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.inntekt.consumer.rs.ConsumerUtils;
import no.nav.registre.inntekt.consumer.rs.InntektstubV2Consumer;
import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;
import no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.Inntektsinformasjon;

@ExtendWith(MockitoExtension.class)
@RestClientTest({InntektstubV2Consumer.class, ConsumerUtils.class})
@ContextConfiguration(classes = {InntektstubV2Consumer.class, ConsumerUtils.class, RestTemplate.class})
@ActiveProfiles("test")
class InntektstubV2ConsumerTest {

    @Autowired
    private InntektstubV2Consumer consumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${inntektstub-u1.rest.api.url}")
    private String serverUrl;

    private List<String> identer;

    @BeforeEach
    public void setUp() {
        identer = Arrays.asList("01010101010", "02020202020");
    }

    @Test
    void shouldHenteEksisterendeIdenter() throws JsonProcessingException {
        var expectedUri = serverUrl + "/v2/personer";
        stubInntektstubHentEksisterendeIdenter(expectedUri);

        var identer = consumer.hentEksisterendeIdenter();

        server.verify();

        assertThat(identer, containsInAnyOrder(identer.get(0), identer.get(1)));
    }

    @Test
    void shouldHenteEksisterendeInntekterPaaIdenter() throws JsonProcessingException {
        var expectedUri = serverUrl + "/v2/inntektsinformasjon?historikk=false&norske-identer={fnr1},{fnr2}";
        stubInntektstubHentEksisterendeInntekter(expectedUri);

        var inntekter = consumer.hentEksisterendeInntekterPaaIdenter(identer);

        server.verify();

        assertThat(inntekter.get(0).getNorskIdent(), equalTo(identer.get(0)));
        assertThat(inntekter.get(1).getNorskIdent(), equalTo(identer.get(1)));
    }

    @Test
    void shouldLeggeInntekterIStub() throws JsonProcessingException {
        Map<String, List<RsInntekt>> identerMedInntekter = new HashMap<>();
        List<RsInntekt> inntekterIdent1 = Collections.singletonList(
                RsInntekt.builder()
                        .aar("2020")
                        .maaned("januar")
                        .inntektstype("Loennsinntekt")
                        .utloeserArbeidsgiveravgift(false)
                        .inngaarIGrunnlagForTrekk(true)
                        .beloep(10_000D)
                        .build());
        List<RsInntekt> inntekterIdent2 = Collections.singletonList(
                RsInntekt.builder()
                        .aar("2019")
                        .maaned("desember")
                        .inntektstype("Naeringsinntekt")
                        .utloeserArbeidsgiveravgift(true)
                        .inngaarIGrunnlagForTrekk(true)
                        .beloep(20_000D)
                        .build());
        identerMedInntekter.put(identer.get(0), inntekterIdent1);
        identerMedInntekter.put(identer.get(1), inntekterIdent2);

        var expectedUri = serverUrl + "/v2/inntektsinformasjon?valider-inntektskombinasjoner=true&valider-kodeverk=true";
        stubInntektstubLeggInntekterIStub(expectedUri);

        var inntekter = consumer.leggInntekterIInntektstub(identerMedInntekter);

        server.verify();

        assertThat(inntekter.get(0).getNorskIdent(), equalTo(identer.get(0)));
        assertThat(inntekter.get(1).getNorskIdent(), equalTo(identer.get(1)));
    }

    private void stubInntektstubHentEksisterendeIdenter(String expectedUri) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(identer)));
    }

    private void stubInntektstubHentEksisterendeInntekter(String expectedUri) throws JsonProcessingException {
        List<Inntektsinformasjon> inntektsinformasjon = Arrays.asList(
                Inntektsinformasjon.builder()
                        .norskIdent(identer.get(0))
                        .build(),
                Inntektsinformasjon.builder()
                        .norskIdent(identer.get(1))
                        .build());
        server.expect(requestToUriTemplate(expectedUri, identer.get(0), identer.get(1)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(inntektsinformasjon)));
    }

    private void stubInntektstubLeggInntekterIStub(String expectedUri) throws JsonProcessingException {
        List<Inntektsinformasjon> inntektsinformasjon = Arrays.asList(
                Inntektsinformasjon.builder()
                        .norskIdent(identer.get(0))
                        .build(),
                Inntektsinformasjon.builder()
                        .norskIdent(identer.get(1))
                        .build());
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ObjectMapper().writeValueAsString(inntektsinformasjon)));
    }
}