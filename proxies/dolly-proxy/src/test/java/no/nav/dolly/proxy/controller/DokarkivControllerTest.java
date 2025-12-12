package no.nav.dolly.proxy.controller;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

@DollySpringBootTest
@Slf4j
class DokarkivControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    void testGetEnvironments() {
        webClient
                .get()
                .uri("/dokarkiv/rest/miljoe")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectHeader().contentType("application/json")
                .expectBody(String[].class)
                .isEqualTo(new String[]{"q1", "q2", "q4"});

    }

}
