package no.nav.testnav.apps.brukerservice.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.nimbusds.jose.jwk.RSAKey;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import wiremock.com.fasterxml.jackson.databind.JsonNode;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import no.nav.testnav.apps.brukerservice.client.dto.OrganisasjonDTO;
import no.nav.testnav.apps.brukerservice.dto.BrukerDTO;
import no.nav.testnav.apps.brukerservice.initializer.H2Initializer;
import no.nav.testnav.apps.brukerservice.initializer.WireMockInitializer;
import no.nav.testnav.apps.brukerservice.repository.UserRepository;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.tokenx.TokenXProperties;
import no.nav.testnav.libs.securitycore.domain.tokenx.WellKnown;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {WireMockInitializer.class, H2Initializer.class})
class BrukerControllerIntegrationTest {

    private static final String FIRST_PERSON_IDENT = "13428823974";
    private static final String SECOND_PERSON_IDENT = "13429923972";

    private static final String TEST_URL_PATH_PREFIX = "/test";

    private static final Comparator<BrukerDTO> BRUKER_DTO_COMPARATOR = (o1, o2) -> o1.brukernavn().compareTo(o2.brukernavn())
            + o1.organisasjonsnummer().compareTo(o2.organisasjonsnummer());
    private static ObjectMapper wiremockObjectMapper = new ObjectMapper();
    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper projectObjectMapper;
    @Autowired
    private WireMockServer wireMockServer;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WebTestClient webClient;
    @MockBean
    private TokenXProperties mockTokenX;
    private KeyPair keyPair;

    @BeforeAll
    static void beforeAll() {
        wiremockObjectMapper = new ObjectMapper();
    }

    @AfterEach
    void cleanUpAfterEachTest() {
        userRepository.deleteAll().block();
        wireMockServer.resetAll();
    }

    @BeforeEach
    void beforeEach() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        keyPair = kpg.generateKeyPair();
        var jwks = Jwks.create(generatePublicJwk((RSAPublicKey) keyPair.getPublic()));

        var tokenXWellKnown = WellKnown.builder().token_endpoint(wireMockServer.baseUrl() + TEST_URL_PATH_PREFIX + "/token").build();

        RSAKey jwk = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .build();

        Mockito.when(mockTokenX.getWellKnownUrl())
                .thenReturn(wireMockServer.baseUrl() + TEST_URL_PATH_PREFIX + "/.well-known/oauth-authorization-server");
        Mockito.when(mockTokenX.getJwk())
                .thenReturn(wiremockObjectMapper.writeValueAsString(jwk.toJSONObject()));

        var dummy_token = new AccessToken("dummy token");

        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/jwks"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(jwks))));

        wireMockServer.stubFor(post(urlEqualTo(TEST_URL_PATH_PREFIX + "/token"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(dummy_token))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/.well-known/oauth-authorization-server"))
                .willReturn(aResponse()
                        .withJsonBody(convertToJsonNode(tokenXWellKnown))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );
    }

    private <T> JsonNode convertToJsonNode(T value) throws Exception {
        return wiremockObjectMapper.readTree(wiremockObjectMapper.writeValueAsString(value));
    }

    @Test
    void should_create_new_user() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(new BrukerDTO(null, "username", "999999999", null, null));
        var responseBody = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var actual = projectObjectMapper.readValue(responseBody, BrukerDTO.class);
        var expected = new BrukerDTO(null, "username", "999999999", null, null);

        assertThat(actual).usingComparator(BRUKER_DTO_COMPARATOR).isEqualTo(expected);
    }

    @Test
    void should_get_forbidden_when_user_has_no_access_to_org() {
        wireMockServer.stubFor(
                get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                        .willReturn(aResponse().withStatus(404))
        );

        var body = BodyInserters.fromValue(new BrukerDTO(null, "username", "999999999", null, null));
        webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void should_create_user_and_get_token_for_user() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(new BrukerDTO(null, "username", "999999999", null, null));
        var userResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var user = projectObjectMapper.readValue(userResponse, BrukerDTO.class);

        var tokenResponse = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var decode = JWT.decode(new String(tokenResponse));

        assertThat(decode.getClaim("id").asString())
                .isEqualTo(user.id());
        assertThat(decode.getClaim("org").asString())
                .isEqualTo(user.organisasjonsnummer());
    }

    @Test
    void should_create_user_and_get_user() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(new BrukerDTO(null, "username", "999999999", null, null));
        var userResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var user = projectObjectMapper.readValue(userResponse, BrukerDTO.class);

        var tokenResponse = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var jwt = new String(tokenResponse);

        var userResponseFromGetEndpoint = webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var actual = projectObjectMapper.readValue(userResponseFromGetEndpoint, BrukerDTO.class);
        var expected = new BrukerDTO(null, "username", "999999999", null, null);

        assertThat(actual).usingComparator(BRUKER_DTO_COMPARATOR).isEqualTo(expected);
    }

    @Test
    void should_get_username_when_taken() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(new BrukerDTO(null, "username", "999999999", null, null));
        webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectStatus()
                .isCreated();

        webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/brukernavn/{brukernavn}").build("username"))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void should_change_username_for_created_person() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(
                new BrukerDTO(null, "username", "999999999", null, null)
        );

        var userResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var user = projectObjectMapper.readValue(userResponse, BrukerDTO.class);

        var tokenResponse = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var jwt = new String(tokenResponse);

        var brukernavnResponse = webClient.patch()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/brukernavn").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .body(BodyInserters.fromValue("username2"))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .returnResult()
                .getResponseBody();

        assertThat(new String(brukernavnResponse)).isEqualTo("username2");

        var userResponseFromGetEndpoint = webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var actual = projectObjectMapper.readValue(userResponseFromGetEndpoint, BrukerDTO.class);

        assertThat(actual.brukernavn()).isEqualTo("username2");
    }

    @Test
    void should_delete_person() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(
                new BrukerDTO(null, "username", "999999999", null, null)
        );

        var userResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var user = projectObjectMapper.readValue(userResponse, BrukerDTO.class);

        var tokenResponse = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var jwt = new String(tokenResponse);

        webClient.delete()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .exchange()
                .expectStatus()
                .is2xxSuccessful();

        webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void must_be_logged_inn_to_delete_person() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var body = BodyInserters.fromValue(
                new BrukerDTO(null, "username", "999999999", null, null)
        );

        var userResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(body)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var user = projectObjectMapper.readValue(userResponse, BrukerDTO.class);

        webClient.delete()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(user.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    void only_user_can_delete_its_own_user() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var firstPerson = BodyInserters.fromValue(
                new BrukerDTO(null, "first username", "999999999", null, null)
        );


        var secondPerson = BodyInserters.fromValue(
                new BrukerDTO(null, "second username", "999999999", null, null)
        );

        var firstUserResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(firstPerson)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var secondUserResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", SECOND_PERSON_IDENT)))
                .body(secondPerson)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var firstUser = projectObjectMapper.readValue(firstUserResponse, BrukerDTO.class);
        var secondUser = projectObjectMapper.readValue(secondUserResponse, BrukerDTO.class);


        var secondUserTokenResponse = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(secondUser.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var jwt = new String(secondUserTokenResponse);

        webClient.delete()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(firstUser.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", SECOND_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    @Test
    void only_user_can_get_user() throws Exception {
        wireMockServer.stubFor(get(urlEqualTo(TEST_URL_PATH_PREFIX + "/api/v1/person/organisasjoner/999999999"))
                .willReturn(aResponse().withJsonBody(convertToJsonNode(new OrganisasjonDTO("Test", "999999999", "AS")))
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
        );

        var firstPerson = BodyInserters.fromValue(
                new BrukerDTO(null, "first username", "999999999", null, null)
        );


        var secondPerson = BodyInserters.fromValue(
                new BrukerDTO(null, "second username", "999999999", null, null)
        );

        var firstUserResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .body(firstPerson)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var secondUserResponse = webClient.post()
                .uri("/api/v1/brukere")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", SECOND_PERSON_IDENT)))
                .body(secondPerson)
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var firstUser = projectObjectMapper.readValue(firstUserResponse, BrukerDTO.class);
        var secondUser = projectObjectMapper.readValue(secondUserResponse, BrukerDTO.class);


        var secondUserTokenResponse = webClient.post()
                .uri(builder -> builder.path("/api/v1/brukere/{id}/token").build(secondUser.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", FIRST_PERSON_IDENT)))
                .exchange()
                .expectBody()
                .returnResult()
                .getResponseBody();

        var jwt = new String(secondUserTokenResponse);

        webClient.get()
                .uri(builder -> builder.path("/api/v1/brukere/{id}").build(firstUser.id()))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtWith(Map.of("pid", SECOND_PERSON_IDENT)))
                .header(UserConstant.USER_HEADER_JWT, jwt)
                .exchange()
                .expectStatus()
                .isForbidden();
    }

    private Map<String, Object> generatePublicJwk(RSAPublicKey rsa) {
        Map<String, Object> values = new HashMap<>();
        values.put("kty", rsa.getAlgorithm());
        values.put("kid", UUID.randomUUID());
        values.put("n", Base64.getUrlEncoder().encodeToString(rsa.getModulus().toByteArray()));
        values.put("e", Base64.getUrlEncoder().encodeToString(rsa.getPublicExponent().toByteArray()));
        values.put("alg", "RS256");
        values.put("use", "sig");
        return values;
    }

    private String jwtWith(Map<String, String> claims) {
        var date = Calendar.getInstance();
        var builder = JWT
                .create()
                .withIssuer(wireMockServer.baseUrl() + TEST_URL_PATH_PREFIX)
                .withIssuedAt(date.getTime())
                .withNotBefore(date.getTime())
                .withAudience("test")
                .withJWTId(UUID.randomUUID().toString())
                .withExpiresAt(new Date(date.getTimeInMillis() + (2 * 60 * 60 * 1000)));
        claims.forEach(builder::withClaim);
        return builder
                .sign(Algorithm.RSA256(null, (RSAPrivateKey) keyPair.getPrivate()));
    }

    private record Jwks(List<Map<String, Object>> keys) {
        private static Jwks create(Map<String, Object> map) {
            return new Jwks(Collections.singletonList(map));
        }
    }
}