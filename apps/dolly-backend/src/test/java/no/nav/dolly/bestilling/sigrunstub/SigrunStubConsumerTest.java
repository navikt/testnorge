package no.nav.dolly.bestilling.sigrunstub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubLignetInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

class SigrunStubConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "111111111";

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private SigrunstubLignetInntektRequest lignetInntektRequest;

    private SigrunstubPensjonsgivendeInntektRequest pensjonsgivendeForFolketrygden;

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    void setup() {

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
}