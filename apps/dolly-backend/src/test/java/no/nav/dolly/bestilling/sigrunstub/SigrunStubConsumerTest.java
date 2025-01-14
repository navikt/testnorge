package no.nav.dolly.bestilling.sigrunstub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubLignetInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yml")
@AutoConfigureWireMock(port = 0)
class SigrunStubConsumerTest {

    private static final String IDENT = "111111111";

    @MockitoBean
    private TokenExchange tokenService;

    @MockitoBean
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private SigrunstubLignetInntektRequest lignetInntektRequest;

    private SigrunstubPensjonsgivendeInntektRequest pensjonsgivendeForFolketrygden;

    private void stubOpprettSkattegrunnlagOK() {

        stubFor(put(urlPathMatching("(.*)/sigrunstub/api/v1/lignetinntekt"))
                .willReturn(ok()
                        .withBody("{\"opprettelseTilbakemeldingsListe\":[{\"status\":200}]}")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubOpprettPensjongivendeOK() {

        stubFor(put(urlPathMatching("(.*)/sigrunstub/api/v1/pensjonsgivendeinntektforfolketrygden"))
                .willReturn(ok()
                        .withBody("{\"opprettelseTilbakemeldingsListe\":[{\"status\":200}]}")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubOpprettSkattegrunnlagMedBadRequest() throws JsonProcessingException {

        stubFor(put(urlPathMatching("(.*)/sigrunstub/api/v1/lignetinntekt"))
                .willReturn(badRequest()
                        .withBody(asJsonString(singletonList(lignetInntektRequest)))
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubDeleteSkattegrunnlagOK() {

        stubFor(delete(urlPathMatching("(.*)/sigrunstub/api/v1/slett"))
                .withHeader("personidentifikator", matching(IDENT))
                .willReturn(ok()
                        .withBody("{}")
                        .withHeader("Content-Type", "application/json")));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    void setup() {

        WireMock.reset();

        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));

        lignetInntektRequest = SigrunstubLignetInntektRequest.builder()
                .inntektsaar("1978")
                .build();

        pensjonsgivendeForFolketrygden = SigrunstubPensjonsgivendeInntektRequest.builder()
                .inntektsaar("1978")
                .build();
    }

    @Test
    void createSkattegrunnlag() {

        stubOpprettSkattegrunnlagOK();

        StepVerifier.create(sigrunStubConsumer.updateLignetInntekt(singletonList(lignetInntektRequest)))
                .expectNext(SigrunstubResponse.builder()
                        .opprettelseTilbakemeldingsListe(List.of(SigrunstubResponse.OpprettelseTilbakemelding.builder()
                                .inntektsaar("1978")
                                .status(200)
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void createPensjonsgivendeOK() {

        stubOpprettPensjongivendeOK();

        StepVerifier.create(sigrunStubConsumer.updatePensjonsgivendeInntekt(singletonList(pensjonsgivendeForFolketrygden)))
                .expectNext(SigrunstubResponse.builder()
                        .opprettelseTilbakemeldingsListe(List.of(SigrunstubResponse.OpprettelseTilbakemelding.builder()
                                .inntektsaar("1978")
                                .status(200)
                                .build()))
                        .build())
                .verifyComplete();
    }

    @Test
    void createSkattegrunnlag_kasterSigrunExceptionHvisKallKasterClientException() throws Exception {

        stubOpprettSkattegrunnlagMedBadRequest();

        StepVerifier.create(sigrunStubConsumer.updateLignetInntekt(singletonList(lignetInntektRequest)))
                .expectNext(SigrunstubResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .melding("[{\"inntektsaar\":\"1978\",\"grunnlag\":[],\"svalbardGrunnlag\":[]}]")
                        .build())
                .verifyComplete();
    }

    @Test
    void deleteSkattegrunnlag_Ok() {

        stubDeleteSkattegrunnlagOK();

        StepVerifier.create(sigrunStubConsumer.deleteLignetInntekt(List.of(IDENT)))
                .expectNext(SigrunstubResponse.builder()
                        .status(HttpStatus.OK)
                        .ident(IDENT)
                        .build())
                .verifyComplete();
    }
}