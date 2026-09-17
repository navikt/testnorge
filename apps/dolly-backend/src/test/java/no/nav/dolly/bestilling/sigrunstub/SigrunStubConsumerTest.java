package no.nav.dolly.bestilling.sigrunstub;

import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubPensjonsgivendeInntektRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubSummertskattegrunnlagRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;

class SigrunStubConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "111111111";

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    private SigrunstubPensjonsgivendeInntektRequest pensjonsgivendeForFolketrygden;

    @BeforeEach
    void setup() {

        pensjonsgivendeForFolketrygden = SigrunstubPensjonsgivendeInntektRequest.builder()
                .inntektsaar("1978")
                .build();
    }

    @Test
    void shouldUpdatePensjonsgivendeInntekt() {

        stubFor(put(urlPathMatching("(.*)/sigrunstub/api/v1/pensjonsgivendeinntektforfolketrygden"))
                .willReturn(ok()
                        .withBody("{\"opprettelseTilbakemeldingsListe\":[{\"status\":200}]}")
                        .withHeader("Content-Type", "application/json")));

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
    void shouldDeletePensjonsgivendeInntekt() {

        stubFor(delete(urlPathMatching("(.*)/sigrunstub/api/v1/pensjonsgivendeinntektforfolketrygden"))
                .withHeader("norskident", matching(IDENT))
                .willReturn(ok()
                        .withBody("{}")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(sigrunStubConsumer.deletePensjonsgivendeInntekt(List.of(IDENT)))
                .expectNext(SigrunstubResponse.builder()
                        .status(HttpStatus.OK)
                        .ident(IDENT)
                        .build())
                .verifyComplete();
    }

    @Test
    void shouldDeleteSummertSkattegrunnlag() {

        stubFor(delete(urlPathMatching("(.*)/sigrunstub/api/v2/summertskattegrunnlag"))
                .withHeader("personidentifikator", matching(IDENT))
                .willReturn(ok()));

        StepVerifier.create(sigrunStubConsumer.deleteSummertSkattegrunnlag(List.of(IDENT)))
                .expectNext(SigrunstubResponse.builder()
                        .status(HttpStatus.OK)
                        .ident(IDENT)
                        .build())
                .verifyComplete();
    }

    @Test
    void shouldCreateSummertSkattegrunnlag() {

        var request = SigrunstubSummertskattegrunnlagRequest.builder()
                .summertskattegrunnlag(List.of(
                        SigrunstubSummertskattegrunnlagRequest.Summertskattegrunnlag.builder()
                                .personidentifikator(IDENT)
                                .inntektsaar("2025")
                                .build()))
                .build();

        stubFor(post(urlPathMatching("(.*)/sigrunstub/api/v2/summertskattegrunnlag"))
                .willReturn(ok()));

        StepVerifier.create(sigrunStubConsumer.createSigrunstubSummertSkattegrunnlag(request))
                .expectNext(SigrunstubResponse.builder()
                        .status(HttpStatus.OK)
                        .ident(IDENT)
                        .build())
                .verifyComplete();
    }

    @Test
    void shouldImportPensjonsgivendeInntektForFolketrygden() {

        verifyImport(
                "/sigrunstub/api/v1/pensjonsgivendeinntektforfolketrygden/import",
                sigrunStubConsumer.importPensjonsgivendeInntektForFolketrygden(IDENT));
    }

    @Test
    void shouldImportSummertSkattegrunnlag() {

        verifyImport(
                "/sigrunstub/api/v1/summertskattegrunnlag/import",
                sigrunStubConsumer.importSummertSkattegrunnlag(IDENT));
    }

    @Test
    void shouldCheckImportPensjonsgivendeInntektForFolketrygden() {

        verifyImport(
                "/sigrunstub/api/v1/pensjonsgivendeinntektforfolketrygden/import/check",
                sigrunStubConsumer.importCheckPensjonsgivendeInntektForFolketrygden(IDENT));
    }

    @Test
    void shouldCheckImportSummertSkattegrunnlag() {

        verifyImport(
                "/sigrunstub/api/v1/summertskattegrunnlag/import/check",
                sigrunStubConsumer.importCheckSummertSkattegrunnlag(IDENT));
    }

    private void verifyImport(String url, reactor.core.publisher.Mono<SigrunstubResponse> response) {

        stubFor(post(urlPathMatching("(.*)" + url))
                .withRequestBody(equalToJson("""
                        {"norskident":"111111111"}"""))
                .willReturn(ok()));

        StepVerifier.create(response)
                .expectNext(SigrunstubResponse.builder()
                        .status(HttpStatus.OK)
                        .ident(IDENT)
                        .build())
                .verifyComplete();
    }
}