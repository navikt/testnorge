package no.nav.dolly.proxy.route;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureNavTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.AzureTrygdeetatenTokenService;
import no.nav.testnav.libs.reactivesecurity.exchange.tokenx.TokenXService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWebTestClient(timeout = "30000")
class RouteLocatorConfigTest {

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

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension
            .newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void setDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("app.fakedings.url", () -> wireMockServer.baseUrl());

        registry.add("app.targets.aareg-services", () -> wireMockServer.baseUrl());
        registry.add("app.targets.aareg-vedlikehold", () -> wireMockServer.baseUrl());
        registry.add("app.targets.arena-forvalteren", () -> wireMockServer.baseUrl());
        registry.add("app.targets.arena-ords", () -> wireMockServer.baseUrl());
        registry.add("app.targets.batch", () -> wireMockServer.baseUrl());
        registry.add("app.targets.brregstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.dokarkiv", () -> wireMockServer.baseUrl());
        registry.add("app.targets.ereg", () -> wireMockServer.baseUrl());
        registry.add("app.targets.fullmakt", () -> wireMockServer.baseUrl());
        registry.add("app.targets.histark", () -> wireMockServer.baseUrl());
        registry.add("app.targets.inntektstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.inst", () -> wireMockServer.baseUrl());
        registry.add("app.targets.kontoregister", () -> wireMockServer.baseUrl());
        registry.add("app.targets.krrstub", () -> wireMockServer.baseUrl());
        registry.add("app.targets.medl", () -> wireMockServer.baseUrl());
        registry.add("app.targets.norg2", () -> wireMockServer.baseUrl());
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
        registry.add("app.targets.udistub", () -> wireMockServer.baseUrl());
    }

    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-tokenx-token")));
        when(navTokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-nav-token")));
        when(trygdeetatenTokenService.exchange(any()))
                .thenReturn(Mono.just(new AccessToken("dummy-trygdeetaten-token")));
        when(tokenXService.exchange(any(), any()))
                .thenReturn(Mono.just(new AccessToken("dummy-tokenx-token")));
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
                    .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));
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
                    .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));
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
                .expectHeader().contentType("application/json;charset=UTF-8")
                .expectBody(String.class).isEqualTo(responseBody);

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(servedPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
    void testFullmakt() {

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

        wireMockServer.verify(1, getRequestedFor(urlEqualTo(downstreamPath))
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-tokenx-token")));

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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                        .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
    @ValueSource(strings = {"q1", "q2"})
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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

    }

    @ParameterizedTest
    @ValueSource(strings = {"q1", "q2"})
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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));


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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-trygdeetaten-token")));

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
                .withHeader(HttpHeaders.AUTHORIZATION, matching("Bearer dummy-nav-token")));

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

}