package no.nav.testnav.apps.persontilgangservice.integrationtest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.nav.testnav.apps.persontilgangservice.controller.dto.OrganisasjonDTO;
import no.nav.testnav.integrationtest.client.TokendingsClient;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.util.Collections.singletonList;

@Tag("integration")
class PersonTilgangServiceIntegrationTest {

    private static final String PID = "01810048413";
    public static MockWebServer mockBackEnd;
    @MockBean
    JwtDecoder jwtDecoder;
    private ObjectMapper objectMapper;
    private WebClient webClient;
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
        tokendingsClient = new TokendingsClient(baseUrl);
        webClient = WebClient.builder().baseUrl(baseUrl).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void should_get_all_organisasjons_user_has_access_to_and_get_a_single_organisasjon() throws JsonProcessingException {

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(new AccessToken("test"))));
        // Opprett token for brukeren
        var token = tokendingsClient.generateToken("dev-gcp:dolly:testnav-person-organisasjon-tilgang-service", PID).block();

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(singletonList(new OrganisasjonDTO("test", "123", "test", LocalDateTime.now())))));
        // Hent alle organiasjoner brukeren har tilgang til
        var organisasjoner = webClient.get()
                .uri("/api/v1/person/organisasjoner")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .retrieve()
                .bodyToMono(OrganisasjonDTO[].class)
                .block();

        Assertions.assertThat(organisasjoner).isNotEmpty();

        mockBackEnd.enqueue(
                new MockResponse().setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(objectMapper.writeValueAsString(new OrganisasjonDTO("test", "123", "test", LocalDateTime.now()))));

        // Hent en organisasjon
        var organisasjon = webClient.get()
                .uri(builder -> builder.path("/api/v1/person/organisasjoner/{organisasjonsnummer}").build(organisasjoner[0].organisasjonsnummer()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token.getTokenValue())
                .retrieve()
                .bodyToMono(OrganisasjonDTO.class)
                .block();

        Assertions.assertThat(organisasjon).isNotNull();
    }


}
