package no.nav.testnav.apps.brukerservice.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.integrationtest.client.TokendingsClient;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Tag("integration")
class BrukerServiceIntegrationTest {

    private static final String PID = "01810048413";
    private static final String ORGNUMMER = "811306312";
    public static MockWebServer mockBackEnd;

    private ObjectMapper objectMapper;
    private WebClient webClient = WebClient.builder().build();
    private TokendingsClient tokendingsClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void initialize() {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());
        tokendingsClient = new TokendingsClient(webClient, baseUrl);
        this.webClient = webClient
                .mutate()
                .baseUrl(baseUrl)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void should_create_new_user_login_change_username_and_then_delete_user() throws JsonProcessingException {

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(new AccessToken("test"))));

        var token = tokendingsClient
                .generateToken("dev-gcp:dolly:testnav-bruker-service", PID)
                .block();
        assert token != null;

        // Create user
        var expected = new BrukerDTO(null, "username", "email", ORGNUMMER, null, null);

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(expected)));

        var bruker = webClient
                .post()
                .uri("/api/v1/brukere")
                .headers(WebClientHeader.bearer(token.getTokenValue()))
                .body(BodyInserters.fromValue(expected))
                .retrieve()
                .bodyToMono(BrukerDTO.class)
                .block();
        assert bruker != null;

        assertThat(bruker)
                .usingRecursiveComparison()
                .comparingOnlyFields("brukernavn", "organisasjonsnummer")
                .isEqualTo(expected);

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("Testing"));

        // Login user
        var userJwt = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(bruker.id()))
                .headers(WebClientHeader.bearer(token.getTokenValue()))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("Testing"));

        // Change username
        webClient.patch()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/brukernavn").build(bruker.id()))
                .headers(WebClientHeader.bearer(token.getTokenValue()))
                .headers(WebClientHeader.jwt(userJwt))
                .body(BodyInserters.fromValue(
                        "new-username"
                ))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(BrukerDTO.builder().brukernavn("new-username").build())));

        // Get updated user
        var updatedUser = webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(bruker.id()))
                .headers(WebClientHeader.bearer(token.getTokenValue()))
                .headers(WebClientHeader.jwt(userJwt))
                .retrieve()
                .bodyToMono(BrukerDTO.class)
                .block();

        assertThat(updatedUser)
                .isNotNull()
                .extracting(BrukerDTO::brukernavn)
                .isEqualTo("new-username");

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody("Testing"));

        // Delete user
        webClient.delete()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(bruker.id()))
                .headers(WebClientHeader.bearer(token.getTokenValue()))
                .headers(WebClientHeader.jwt(userJwt))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
