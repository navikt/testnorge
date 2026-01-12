package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.bestilling.AbstractConsumerTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class PersonServiceConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "01010101010";

    @Autowired
    private PersonServiceConsumer personServiceConsumer;

    @Autowired
    private JsonMapper jsonMapper;

    @Test
    void shouldVerifyJsonMapperType() {
        System.out.println("JsonMapper type: " + jsonMapper.getClass().getName());
        assertThat(jsonMapper).isInstanceOf(JsonMapper.class);
    }

    @Test
    void shouldReturnTrueWhenPersonExists() {
        stubFor(get(urlPathMatching("(.*)/api/v1/personer/" + IDENT + "/exists"))
                .willReturn(ok()
                        .withBody("true")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(personServiceConsumer.isPerson(IDENT, Set.of()))
                .assertNext(response -> {
                    System.out.println("Response: ident=" + response.getIdent() + 
                            ", exists=" + response.getExists() + 
                            ", status=" + response.getStatus() + 
                            ", feilmelding=" + response.getFeilmelding());
                    
                    assertThat(response.getIdent()).isEqualTo(IDENT);
                    
                    if (response.getStatus().is5xxServerError() || !response.getStatus().is2xxSuccessful()) {
                        System.out.println("ERROR: Got non-2xx status: " + response.getStatus());
                        System.out.println("Feilmelding: " + response.getFeilmelding());
                        assertThat(response.getStatus())
                                .describedAs("Expected 2xx status but got %s with feilmelding: %s", 
                                        response.getStatus(), response.getFeilmelding())
                                .isEqualTo(HttpStatus.OK);
                    }
                    
                    assertThat(response.getExists())
                            .describedAs("exists should be true but was %s, status: %s, feilmelding: %s", 
                                    response.getExists(), response.getStatus(), response.getFeilmelding())
                            .isTrue();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnFalseWhenPersonDoesNotExist() {
        stubFor(get(urlPathMatching("(.*)/api/v1/personer/" + IDENT + "/exists"))
                .willReturn(ok()
                        .withBody("false")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(personServiceConsumer.isPerson(IDENT, Set.of()))
                .assertNext(response -> {
                    System.out.println("Response: ident=" + response.getIdent() + 
                            ", exists=" + response.getExists() + 
                            ", status=" + response.getStatus() + 
                            ", feilmelding=" + response.getFeilmelding());
                            
                    assertThat(response.getIdent()).isEqualTo(IDENT);
                    assertThat(response.getStatus()).isNotNull();
                    assertThat(response.getStatus().is2xxSuccessful())
                            .describedAs("Status should be 2xx but was %s, feilmelding: %s", 
                                    response.getStatus(), response.getFeilmelding())
                            .isTrue();
                    assertThat(response.getExists())
                            .describedAs("exists should be false, status: %s, feilmelding: %s", 
                                    response.getStatus(), response.getFeilmelding())
                            .isFalse();
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleServerError() {
        stubFor(get(urlPathMatching("(.*)/api/v1/personer/" + IDENT + "/exists"))
                .willReturn(serverError()
                        .withBody("Internal Server Error")
                        .withHeader("Content-Type", "text/plain")));

        StepVerifier.create(personServiceConsumer.isPerson(IDENT, Set.of()))
                .assertNext(response -> {
                    assertThat(response.getExists()).isFalse();
                    assertThat(response.getStatus().is5xxServerError()).isTrue();
                    assertThat(response.getIdent()).isEqualTo(IDENT);
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleOpplysningIdParameter() {
        stubFor(get(urlPathMatching("(.*)/api/v1/personer/" + IDENT + "/exists"))
                .willReturn(ok()
                        .withBody("true")
                        .withHeader("Content-Type", "application/json")));

        StepVerifier.create(personServiceConsumer.isPerson(IDENT, Set.of("opplysning1", "opplysning2")))
                .assertNext(response -> {
                    assertThat(response.getExists()).isTrue();
                    assertThat(response.getStatus().is2xxSuccessful()).isTrue();
                })
                .verifyComplete();
    }
}

