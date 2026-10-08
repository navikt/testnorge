package no.nav.dolly.proxy.route;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.proxy.service.DokarkivUploadService;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.cloud.gateway.handler.FilteringWebHandler;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.CRC32;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.absent;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@DollySpringBootTest
@AutoConfigureWebTestClient(timeout = "30000")
class RouteLocatorConfigTest {

    private static final String TOKEN = JWT
            .create()
            .withExpiresAt(Date.from(Instant.now().plusSeconds(TimeUnit.MINUTES.toSeconds(5))))
            .sign(Algorithm.none());

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort().stubRequestLoggingDisabled(true))
            .build();
    @MockitoBean
    private TokenExchange tokenExchange;
    @MockitoSpyBean
    private AzureNavTokenService navTokenService;
    @MockitoSpyBean
    private AzureTrygdeetatenTokenService trygdeetatenTokenService;
    @MockitoBean
    private TokenXService tokenXService;
    @Autowired
    private WebTestClient webClient;
    @Autowired
    private DokarkivUploadService uploadService;
    @Autowired
    private RouteLocator routeLocator;

    @BeforeEach
    void setup() {
        when(tokenExchange.exchange(any()))
                .thenReturn(Mono.just(new AccessToken(TOKEN)));
        when(navTokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken(TOKEN)));
        when(trygdeetatenTokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken(TOKEN)));
        when(tokenXService.exchange(any(), any()))
                .thenReturn(Mono.just(new AccessToken(TOKEN)));
    }

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("app.fakedings.url", () -> wireMockServer.baseUrl());

        registry.add("app.targets.aareg-services", () -> wireMockServer.baseUrl());
        registry.add("app.targets.aareg-vedlikehold", () -> wireMockServer.baseUrl());
        registry.add("app.targets.arena-forvalteren", () -> wireMockServer.baseUrl());
        registry.add("app.targets.arena-ords", () -> wireMockServer.baseUrl());
        registry.add("app.targets.batch", () -> wireMockServer.baseUrl());
        registry.add("app.targets.oppfoelgingsvedtak14a", () -> wireMockServer.baseUrl());
        registry.add("app.targets.brregstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.dokarkiv", () -> wireMockServer.baseUrl());
        registry.add("app.targets.ereg", () -> wireMockServer.baseUrl());
        registry.add("app.targets.fullmakt", () -> wireMockServer.baseUrl());
        registry.add("app.targets.histark", () -> wireMockServer.baseUrl());
        registry.add("app.targets.inntektstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.inst", () -> wireMockServer.baseUrl());
        registry.add("app.targets.kelvin-aap", () -> wireMockServer.baseUrl());
        registry.add("app.targets.kontoregister", () -> wireMockServer.baseUrl());
        registry.add("app.targets.skattekort-q1", () -> wireMockServer.baseUrl());
        registry.add("app.targets.skattekort-q2", () -> wireMockServer.baseUrl());
        registry.add("app.targets.krrstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.medl", () -> wireMockServer.baseUrl());
        registry.add("app.targets.norg2", () -> wireMockServer.baseUrl());
        registry.add("app.targets.oppfoelging", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pdl-api", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pdl-api-q1", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pdl-identhendelse", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pdl-testdata", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pensjon", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pensjon-afp", () -> wireMockServer.baseUrl());
        registry.add("app.targets.pensjon-samboer", () -> wireMockServer.baseUrl());
        registry.add("app.targets.saf", () -> wireMockServer.baseUrl());
        registry.add("app.targets.sigrunstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.skjermingsregister", () -> wireMockServer.baseUrl());
        registry.add("app.targets.sykemelding", () -> wireMockServer.baseUrl());
        registry.add("app.targets.udistub", () -> wireMockServer.baseUrl());
    }

    @ParameterizedTest
    @CsvSource({
            "q1,false",
            "q2,false",
            "q4,false",
            "q1,true",
            "q2,true",
            "q4,true"
    })
    void testAareg(String miljo, boolean writeable) {

        var downstreamPath = "/some/aareg/path";
        var responseBody = "Success from mocked aareg-%s-%s".formatted(writeable ? "write" : "read", miljo);

        if (writeable) {

            wireMockServer.stubFor(post(urlEqualTo(downstreamPath))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody(responseBody)));

            webClient
                    .post()
                    .uri("/aareg/" + miljo + downstreamPath)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("application/json")
                    .expectBody(String.class).isEqualTo(responseBody);

            wireMockServer.verify(1, postRequestedFor(urlEqualTo(downstreamPath))
                    .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));
            wireMockServer.verify(0, getRequestedFor(urlEqualTo(downstreamPath)));

        } else {

            wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "text/plain")
                            .withBody(responseBody)));

            webClient
                    .get()
                    .uri("/aareg/" + miljo + downstreamPath)
                    .exchange()
                    .expectStatus().isOk()
                    .expectHeader().contentType("text/plain")
                    .expectBody(String.class).isEqualTo(responseBody);

            wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                    .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));
            wireMockServer.verify(0, postRequestedFor(urlEqualTo(downstreamPath)));

        }


    }

    @Test
    void testArenaOrds() {

        var downstreamPath = "/api/test";
        var responseBody = "Success from mocked arena";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/arena" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2", "q4"})
    void testArenaForvalteren(String miljo) {

        var downstreamPath = "/some/arena/forvalteren/path";
        var responseBody = "Success from mocked arena-%s".formatted(miljo);

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/arena/" + miljo + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }


    @Test
    void testBatch() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked batch";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/batch" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testBrregstub() {

        var downstreamPath = "/api/foobar/1337";
        var responseBody = "Success from mocked brregstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/brregstub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testOppfoelgingsvedtak14a() {

        var downstreamPath = "/api/v1/vedtak";
        var responseBody = "Success from mocked oppfoelgingsvedtak14a";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/oppfoelgingsvedtak14a" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2", "q4"})
    void testDokarkiv(String env) {

        var requestedPath = "/dokarkiv/api/%s/some/path".formatted(env);
        var servedPath = "/rest/journalpostapi/some/path";
        var responseBody = "Success from mocked dokarkiv-%s".formatted(env);

        wireMockServer.stubFor(get(urlEqualTo(servedPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/text")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri(requestedPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json; charset=UTF-8")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(servedPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @RepeatedTest(3)
    void shouldForwardEightyMiBDocumentsToQ1AndQ2Concurrently() throws Exception {
        var content = Base64.getEncoder().encodeToString(new byte[80 * 1024 * 1024]);
        var uploadIds = new HashMap<String, String>();
        for (var environment : List.of("q1", "q2")) {
            var uploadId = uploadService.initUpload();
            uploadIds.put(environment, uploadId);
            for (int offset = 0; offset < content.length(); offset += 500_000) {
                uploadService.appendChunk(uploadId, content.substring(offset, Math.min(offset + 500_000, content.length())));
            }
        }
        var servedPath = "/rest/journalpostapi/v1/journalpost?forsoekFerdigstill=false";
        wireMockServer.stubFor(post(urlEqualTo(servedPath))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"journalpostId\":\"journalpost\"}")));

        try (var executor = Executors.newFixedThreadPool(2)) {
            var requests = uploadIds.entrySet().stream()
                    .map(entry -> CompletableFuture.runAsync(() -> webClient.post()
                            .uri("/dokarkiv/api/" + entry.getKey() + "/v1/journalpost?forsoekFerdigstill=false")
                            .bodyValue(Map.of("dokumenter", List.of(Map.of("dokumentvarianter",
                                    List.of(Map.of("filtype", "PDF", "variantformat", "ARKIV", "uploadReferanse", entry.getValue()))))))
                            .exchange()
                            .expectStatus().isOk()
                            .expectBody()
                            .jsonPath("$.journalpostId").isEqualTo("journalpost"), executor))
                    .toArray(CompletableFuture[]::new);
            CompletableFuture.allOf(requests).get(30, TimeUnit.SECONDS);
        }

        wireMockServer.verify(2, postRequestedFor(urlEqualTo(servedPath)));
        wireMockServer.getAllServeEvents().stream()
                .filter(event -> event.getRequest().getUrl().equals(servedPath))
                .forEach(event -> {
                    var forwardedBody = event.getRequest().getBodyAsString();
                    var contentPrefix = "\"fysiskDokument\":\"";
                    var contentStart = forwardedBody.indexOf(contentPrefix) + contentPrefix.length();
                    assertThat(contentStart).isGreaterThanOrEqualTo(contentPrefix.length());
                    assertThat(forwardedBody.regionMatches(contentStart, content, 0, content.length())).isTrue();
                    assertThat(forwardedBody.charAt(contentStart + content.length())).isEqualTo('"');
                    assertThat(forwardedBody.contains("uploadReferanse")).isFalse();
                });
    }

    @Test
    void shouldWriteEightyMiBDocumentsToQ1AndQ2InBoundedBuffers() {
        var encodedLength = 4 * Math.ceilDiv(80 * 1024 * 1024, 3);
        var prefix = "{\"dokumenter\":[{\"dokumentvarianter\":[{\"filtype\":\"PDF\",\"variantformat\":\"ARKIV\",\"fysiskDokument\":\"";
        var suffix = "\"}]}]}";
        var expectedChecksum = new CRC32();
        expectedChecksum.update(prefix.getBytes(UTF_8));
        var uploadIds = Map.of("q1", uploadService.initUpload(), "q2", uploadService.initUpload());
        var fullChunk = "A".repeat(500_000);
        for (int offset = 0; offset < encodedLength; offset += fullChunk.length()) {
            var remaining = encodedLength - offset;
            var chunk = remaining > fullChunk.length() ? fullChunk : "A".repeat(remaining - 1) + "=";
            expectedChecksum.update(chunk.getBytes(UTF_8));
            uploadIds.values().forEach(uploadId -> uploadService.appendChunk(uploadId, chunk));
        }
        expectedChecksum.update(suffix.getBytes(UTF_8));
        var expectedLength = prefix.length() + encodedLength + suffix.length();

        Flux.fromIterable(uploadIds.entrySet())
                .flatMap(entry -> routeLocator.getRoutes()
                        .filter(route -> route.getId().equals("dokarkiv-" + entry.getKey()))
                        .single()
                        .flatMap(route -> {
                            var requestBody = "{\"dokumenter\":[{\"dokumentvarianter\":[{\"filtype\":\"PDF\",\"variantformat\":\"ARKIV\",\"uploadReferanse\":\""
                                    + entry.getValue() + "\"}]}]}";
                            var exchange = MockServerWebExchange.from(MockServerHttpRequest
                                    .post("/dokarkiv/api/" + entry.getKey() + "/v1/journalpost?forsoekFerdigstill=false")
                                    .contentType(APPLICATION_JSON)
                                    .body(requestBody));
                            exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, route);
                            var handler = new FilteringWebHandler(List.of((forwardedExchange, chain) ->
                                    assertForwardedDocument(forwardedExchange, expectedLength, expectedChecksum.getValue())), false);
                            return handler.handle(exchange).subscribeOn(Schedulers.parallel());
                        }))
                .then()
                .block(Duration.ofSeconds(30));
    }

    private Mono<Void> assertForwardedDocument(ServerWebExchange exchange, int expectedLength, long expectedChecksum) {
        var request = exchange.getRequest();
        assertThat(request.getURI().getPath()).isEqualTo("/rest/journalpostapi/v1/journalpost");
        assertThat(request.getURI().getQuery()).isEqualTo("forsoekFerdigstill=false");
        assertThat(request.getHeaders().getContentType()).isEqualTo(APPLICATION_JSON);
        assertThat(request.getHeaders().getContentLength()).isEqualTo(expectedLength);
        assertThat(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).isEqualTo("Bearer " + TOKEN);
        var checksum = new CRC32();
        var receivedLength = new AtomicInteger();
        var largestBuffer = new AtomicInteger();
        return request.getBody()
                .limitRate(1)
                .doOnNext(buffer -> {
                    try (var byteBuffers = buffer.readableByteBuffers()) {
                        receivedLength.addAndGet(buffer.readableByteCount());
                        largestBuffer.accumulateAndGet(buffer.readableByteCount(), Math::max);
                        byteBuffers.forEachRemaining(checksum::update);
                    } finally {
                        DataBufferUtils.release(buffer);
                    }
                })
                .then(Mono.fromRunnable(() -> {
                    assertThat(receivedLength.get()).isEqualTo(expectedLength);
                    assertThat(checksum.getValue()).isEqualTo(expectedChecksum);
                    assertThat(largestBuffer.get()).isBetween(1, 64 * 1024);
                }));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "{\"tittel\":\"En journalpost\"}"})
    void shouldForwardDokarkivRequestsWithoutUploadReferences(String body) {
        var servedPath = "/rest/journalpostapi/v1/journalpost";
        wireMockServer.stubFor(post(urlEqualTo(servedPath))
                .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("{}")));

        webClient.post()
                .uri("/dokarkiv/api/q1/v1/journalpost")
                .contentType(APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();

        wireMockServer.verify(1, postRequestedFor(urlEqualTo(servedPath))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo("application/json"))
                .withRequestBody(body.isEmpty() ? absent() : equalTo(body)));
    }

    @Test
    void shouldRejectUnknownDokarkivUploadReference() {
        webClient.post()
                .uri("/dokarkiv/api/q1/v1/journalpost")
                .bodyValue(Map.of("dokumenter", List.of(Map.of("dokumentvarianter",
                        List.of(Map.of("uploadReferanse", "unknown-upload"))))))
                .exchange()
                .expectStatus().isBadRequest();

        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/rest/journalpostapi/v1/journalpost")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2", "q4"})
    void testEreg(String miljo) {

        var requestedPath = "/api/%s/some/nested/path".formatted(miljo);
        var servedPath = "/some/nested/path";
        var responseBody = "Success from mocked ereg-" + miljo;

        wireMockServer.stubFor(get(urlEqualTo(servedPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/ereg" + requestedPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(servedPath)));

    }

    @Test
    void shouldRouteFullmaktWithCurrentTokenXAudience() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked fullmakt";

        wireMockServer.stubFor(get(urlMatching("/fake/tokenx.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("dummy-fakedings-token")));

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/fullmakt" + downstreamPath)
                .header("fnr", "12345678901")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json; charset=UTF-8")
                .expectBody(String.class).isEqualTo(responseBody);

        verify(tokenXService).exchange(
                argThat(properties -> "dev-gcp:repr:fullmakt".equals(properties.toTokenXScope())),
                eq("dummy-fakedings-token"));
        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testHistark() {

        var downstreamPath = "/api/saksmapper/1";
        var responseBody = "Success from mocked histark";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/histark" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testInntektstub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked inntektstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/inntektstub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testInst() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked inst";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/inst" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testKelvinAapGet() {

        var downstreamPath = "/api/test/behandlingStatus";
        var responseBody = "Success from mocked kelvin-aap";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/kelvin-aap" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testKelvinAapPost() {

        var downstreamPath = "/api/test/opprettOgFullfoerBehandling";
        var responseBody = "{\"saksnummer\": \"SAK-123456\"}";

        wireMockServer.stubFor(post(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .post()
                .uri("/kelvin-aap" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, postRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testKontoregister() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked kontoregister";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/kontoregister" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @ParameterizedTest
    @ValueSource(strings = { "q1", "q2" })
    void testSkattekort(String env) {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked skattekort";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/skattekort/%s/%s".formatted(env, downstreamPath))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }


    @Test
    void testKrrstub() {

        var downstreamPath = "/api/v2/something";
        var responseBody = "Success from mocked krrstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/krrstub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testMedl() {

        var downstreamPath = "/rest/v1/someendpoint";
        var responseBody = "Success from mocked medl";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/medl" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testNorg2() {

        var downstreamPath = "/some/nested/path";
        var responseBody = "Success from mocked norg2";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/norg2" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @ParameterizedTest
    @EnumSource(Pdl.SpecialCase.class)
    void testPdl(Pdl.SpecialCase env) {

        var url = "/some/path";
        var responseBody = "Success from mocked " + env.getName();
        switch (env) {

            case Pdl.SpecialCase.API -> {

                wireMockServer.stubFor(get(urlEqualTo(url))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody(responseBody)));

                webClient
                        .get()
                        .uri("/pdl-api" + url)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType("text/plain")
                        .expectBody(String.class).isEqualTo(responseBody);

                wireMockServer.verify(1, getRequestedFor(urlEqualTo(url))
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

            }

            case Pdl.SpecialCase.API_Q1 -> {

                wireMockServer.stubFor(get(urlEqualTo(url))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody(responseBody)));

                webClient
                        .get()
                        .uri("/pdl-api-q1" + url)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType("text/plain")
                        .expectBody(String.class).isEqualTo(responseBody);

                wireMockServer.verify(1, getRequestedFor(urlEqualTo(url))
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

            }

            case Pdl.SpecialCase.IDENTHENDELSE -> {

                wireMockServer.stubFor(get(urlEqualTo(url))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody(responseBody)));

                webClient
                        .get()
                        .uri("/pdl-identhendelse" + url)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType("text/plain")
                        .expectBody(String.class).isEqualTo(responseBody);

                wireMockServer.verify(1, getRequestedFor(urlEqualTo(url))
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("apikey")));

            }

            case Pdl.SpecialCase.TESTDATA -> {

                wireMockServer.stubFor(get(urlEqualTo(url))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody(responseBody)));

                webClient
                        .get()
                        .uri("/pdl-testdata" + url)
                        .exchange()
                        .expectStatus().isOk()
                        .expectHeader().contentType("text/plain")
                        .expectBody(String.class).isEqualTo(responseBody);

                wireMockServer.verify(1, getRequestedFor(urlEqualTo(url))
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

            }

        }

    }

    @Test
    void testPensjon() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked pensjon";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/pensjon" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("dolly"))); // Note use of placeholder here.

    }

    @ParameterizedTest
    @ValueSource(strings = { "q1", "q2" })
    void testPensjonAfp(String env) {

        var downstreamPath = "/api/mock-oppsett/test";
        var responseBody = "Success from mocked pensjon";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/pensjon/%s/%s".formatted(env, downstreamPath))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @ParameterizedTest
    @ValueSource(strings = { "q1", "q2" })
    void testPensjonSamboer(String env) {

        var downstreamPath = "/api/samboer/test";
        var responseBody = "Success from mocked pensjon";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/pensjon/%s/%s".formatted(env, downstreamPath))
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2", "q4"})
    void testSaf(String env) {

        var downstreamPath = "/some/random/path";
        var responseBody = "Success from mocked saf-%s".formatted(env);

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/saf/" + env + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));


    }

    @Test
    void testSigrunstub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked sigrunstub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/sigrunstub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath)));

    }

    @Test
    void testSkjermingsregister() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked skjermingsregister";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/skjermingsregister" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testSykemeldingGet() {

        var downstreamPath = "/api/v1/sykmelding";
        var responseBody = "Success from mocked sykemelding";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/sykemelding" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testSykemeldingPost() {

        var downstreamPath = "/api/v1/sykmelding";
        var responseBody = "{\"id\": \"sykemelding-123\"}";

        wireMockServer.stubFor(post(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .post()
                .uri("/sykemelding" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, postRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testUdistub() {

        var downstreamPath = "/api/v1/testdata";
        var responseBody = "Success from mocked udistub";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/udistub" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json; charset=UTF-8")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

    @Test
    void testInternalEndpoints() {

        webClient
                .get()
                .uri("/internal/health")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/vnd.spring-boot.actuator.v3+json")
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");

        wireMockServer.verify(0, getRequestedFor(urlEqualTo("/internal/health")));

    }

    @Test
    void testNotFound() {

        webClient
                .get()
                .uri("/non-existing-service/some/path")
                .exchange()
                .expectStatus()
                .isNotFound();

        wireMockServer.verify(0, getRequestedFor(urlMatching("/non-existing-service/.*")));

    }

    @Test
    void testOppfoelging() {

        var downstreamPath = "/api/v2/person/hent";
        var responseBody = "Success from mocked oppfoelging";

        wireMockServer.stubFor(get(urlEqualTo(downstreamPath))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/plain")
                        .withBody(responseBody)));

        webClient
                .get()
                .uri("/oppfoelging" + downstreamPath)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/plain")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer " + TOKEN)));

    }

}
