package no.nav.dolly.appservices.tpsf.restcom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.config.credentials.TpsForvalterenProxyProperties;
import no.nav.dolly.domain.resultset.tpsf.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsfBestilling;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.testnav.libs.servletsecurity.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static no.nav.dolly.domain.resultset.IdentType.FNR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class TpsfServiceTest {

    private static final TpsfBestilling STANDARD_TPSF_BESTILLING = TpsfBestilling.builder().identtype(FNR).build();
    private static final String STANDARD_IDENT = "123";
    private static final List<String> STANDARD_IDENTER = new ArrayList<>(singleton(STANDARD_IDENT));
    private static final List<String> STANDARD_MILJOER_U1_T1 = Arrays.asList("u1", "t1");


    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private TpsfService tpsfService;

    @BeforeEach
    public void setup() {

        WireMock.reset();

        when(tokenService.generateToken(ArgumentMatchers.any(TpsForvalterenProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    public void opprettPersonerTpsf_hvisSuksessfultKallReturnerListeAvStringIdenter() throws JsonProcessingException {

        stubPostTpsfDataReturnsOk();

        when(objectMapper.convertValue(anyList(), eq(List.class))).thenReturn(singletonList(FNR));

        List<String> response = tpsfService.opprettIdenterTpsf(STANDARD_TPSF_BESTILLING);

        assertThat(response.get(0), is(STANDARD_IDENT));
    }

    @Test
    public void opprettPersonerTpsf_hvisTpsfKasterExceptionSaaKastesTpsfException() throws Exception {

        WebClientResponseException failure = WebClientResponseException.create(500, "Error", null, null, null);

        stubPostTpsfDataThrowExpection();

        when(objectMapper.readValue(any(byte[].class), eq(WebClientResponseException.class))).thenReturn(failure);

        Assertions.assertThrows(TpsfException.class, () ->
                tpsfService.opprettIdenterTpsf(STANDARD_TPSF_BESTILLING));
    }

    @Test
    public void sendIdenterTilTpsFraTPSF_hvisTpsfKasterExceptionSaaKastesTpsfException() throws Exception {

        WebClientResponseException failure = WebClientResponseException.create(500, "Error", null, null, null);

        stubPostTpsfDataForFlereThrowExpection();

        when(objectMapper.readValue(any(byte[].class), eq(WebClientResponseException.class))).thenReturn(failure);

        Assertions.assertThrows(TpsfException.class, () ->
                tpsfService.sendIdenterTilTpsFraTPSF(STANDARD_IDENTER, STANDARD_MILJOER_U1_T1));
    }

    @Test
    public void sendIdenterTilTpsFraTPSF_hvisIngenMiljoerErSpesifisertSaaKastesIllegalArgumentException() {
        List<String> tomListe = new ArrayList<>();
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                tpsfService.sendIdenterTilTpsFraTPSF(STANDARD_IDENTER, tomListe));
    }

    @Test
    public void sendTilTpsFraTPSF_happyPath() throws JsonProcessingException {

        RsSkdMeldingResponse meldingResponse = RsSkdMeldingResponse.builder().gruppeid(1L).build();

        stubPostTpsfDataForFlereReturnsOk(meldingResponse);

        when(objectMapper.convertValue(anyMap(), eq(RsSkdMeldingResponse.class))).thenReturn(meldingResponse);

        RsSkdMeldingResponse response = tpsfService.sendIdenterTilTpsFraTPSF(STANDARD_IDENTER, STANDARD_MILJOER_U1_T1);

        assertThat(response.getGruppeid(), is(meldingResponse.getGruppeid()));
    }

    private void stubPostTpsfDataForFlereThrowExpection() {

        stubFor(post(urlPathMatching("(.*)/tpsf/api/v1/dolly/testdata/tilTpsFlere"))
                .willReturn(serverError()
                        .withHeader("Content-Type", "application/json")
                        .withBody(String.valueOf(List.of(STANDARD_IDENT)))
                ));
    }

    private void stubPostTpsfDataForFlereReturnsOk(RsSkdMeldingResponse meldingResponse) throws JsonProcessingException {

        stubFor(post(urlPathMatching("(.*)/tpsf/api/v1/dolly/testdata/tilTpsFlere"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(meldingResponse))
                ));
    }

    private void stubPostTpsfDataThrowExpection() {

        stubFor(post(urlPathMatching("(.*)/tpsf/api/v1/dolly/testdata/personer"))
                .willReturn(serverError()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{}")
                ));
    }

    private void stubPostTpsfDataReturnsOk() throws JsonProcessingException {

        stubFor(post(urlPathMatching("(.*)/tpsf/api/v1/dolly/testdata/personer"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(asJsonString(List.of(STANDARD_IDENT)))
                ));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}