package no.nav.dolly.bestilling.personservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.codec.json.JacksonJsonDecoder;
import org.springframework.http.codec.json.JacksonJsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

class Jackson3BooleanDeserializationTest {

    private static WireMockServer wireMockServer;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterAll
    static void teardown() {
        wireMockServer.stop();
    }

    @Test
    void shouldDeserializeBooleanWithJackson3() {
        stubFor(get(urlEqualTo("/test/boolean"))
                .willReturn(ok()
                        .withBody("true")
                        .withHeader("Content-Type", "application/json")));

        var jsonMapper = JsonMapper.builder().build();

        var exchangeStrategies = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();

        var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .exchangeStrategies(exchangeStrategies)
                .build();

        StepVerifier.create(webClient.get()
                        .uri("/test/boolean")
                        .retrieve()
                        .toEntity(Boolean.class))
                .assertNext(response -> {
                    System.out.println("Status: " + response.getStatusCode());
                    System.out.println("Body: " + response.getBody());
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(response.getBody()).isTrue();
                })
                .verifyComplete();
    }

    @Test
    void shouldDeserializeBooleanFalseWithJackson3() {
        stubFor(get(urlEqualTo("/test/boolean-false"))
                .willReturn(ok()
                        .withBody("false")
                        .withHeader("Content-Type", "application/json")));

        var jsonMapper = JsonMapper.builder().build();

        var exchangeStrategies = ExchangeStrategies.builder()
                .codecs(config -> {
                    config.defaultCodecs().maxInMemorySize(32 * 1024 * 1024);
                    config.defaultCodecs().jacksonJsonDecoder(new JacksonJsonDecoder(jsonMapper));
                    config.defaultCodecs().jacksonJsonEncoder(new JacksonJsonEncoder(jsonMapper));
                })
                .build();

        var webClient = WebClient.builder()
                .baseUrl("http://localhost:" + wireMockServer.port())
                .exchangeStrategies(exchangeStrategies)
                .build();

        StepVerifier.create(webClient.get()
                        .uri("/test/boolean-false")
                        .retrieve()
                        .toEntity(Boolean.class))
                .assertNext(response -> {
                    System.out.println("Status: " + response.getStatusCode());
                    System.out.println("Body: " + response.getBody());
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
                    assertThat(response.getBody()).isFalse();
                })
                .verifyComplete();
    }

    @Test
    void shouldDeserializeDirectlyWithJsonMapper() throws Exception {
        var jsonMapper = JsonMapper.builder().build();
        
        Boolean result = jsonMapper.readValue("true", Boolean.class);
        System.out.println("Direct deserialization result: " + result);
        assertThat(result).isTrue();
    }
}

