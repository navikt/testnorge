package no.nav.dolly.bestilling.personservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;

class PersonServiceConsumerTest extends AbstractConsumerTest {

    private static final int BLOCK_SIZE = 100;

    private static final String PDL_PERSON_BOLK_RESPONSE = """
            {
                "data": {
                    "hentPersonBolk": [
                        {
                            "ident": "12345678901",
                            "person": {
                                "navn": [{ "fornavn": "Test", "etternavn": "Testesen" }]
                            },
                            "code": "ok"
                        }
                    ],
                    "hentGeografiskTilknytningBolk": [],
                    "hentIdenterBolk": []
                }
            }
            """;

    @Autowired
    private PersonServiceConsumer personServiceConsumer;

    @AfterEach
    void cleanUp() {
        WireMock.reset();
    }

    @Test
    void shouldHandleExactlyBlockSizeIdenterWithSingleRequest() {
        stubPdlPersonerEndpoint();

        var identer = generateIdenter(BLOCK_SIZE);

        personServiceConsumer.getPdlPersoner(identer)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isNotEmpty())
                .verifyComplete();

        verify(1, getRequestedFor(urlPathMatching("(.*)/api/v2/personer/identer")));
    }

    @Test
    void shouldSplitIntoTwoRequestsWhenIdenterExceedBlockSize() {
        stubPdlPersonerEndpoint();

        var identer = generateIdenter(BLOCK_SIZE + 1);

        personServiceConsumer.getPdlPersoner(identer)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isNotEmpty())
                .verifyComplete();

        verify(2, getRequestedFor(urlPathMatching("(.*)/api/v2/personer/identer")));
    }

    @Test
    void shouldHandleEmptyIdenterListWithNoRequests() {
        stubPdlPersonerEndpoint();

        personServiceConsumer.getPdlPersoner(List.of())
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isEmpty())
                .verifyComplete();

        verify(0, getRequestedFor(urlPathMatching("(.*)/api/v2/personer/identer")));
    }

    @Test
    void shouldHandleExactlyDoubleBlockSizeWithTwoRequests() {
        stubPdlPersonerEndpoint();

        var identer = generateIdenter(BLOCK_SIZE * 2);

        personServiceConsumer.getPdlPersoner(identer)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(results -> assertThat(results).isNotEmpty())
                .verifyComplete();

        verify(2, getRequestedFor(urlPathMatching("(.*)/api/v2/personer/identer")));
    }

    private List<String> generateIdenter(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> String.format("%011d", i))
                .toList();
    }

    private void stubPdlPersonerEndpoint() {
        stubFor(get(urlPathMatching("(.*)/api/v2/personer/identer"))
                .willReturn(ok()
                        .withBody(PDL_PERSON_BOLK_RESPONSE)
                        .withHeader("Content-Type", "application/json")));
    }
}
