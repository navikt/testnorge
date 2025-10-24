package no.nav.registre.testnorge.miljoerservice.provider;

import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@DollySpringBootTest
class MiljoerControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockitoBean
    private SecurityFilterChain securityFilterChain;

    @Test
    void hentAktiveMiljoer() {

        webClient
                .get()
                .uri("/api/v1/miljoer")
                .exchange()
                .expectStatus().isOk()
                .expectBody(List.class)
                .isEqualTo(List.of("q1", "q2", "q4", "qx"));

    }

}
