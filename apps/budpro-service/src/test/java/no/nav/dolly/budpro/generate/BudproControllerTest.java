package no.nav.dolly.budpro.generate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.budpro.navn.GeneratedNameService;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@Disabled("Awaiting rewrite of BudProService to fully reactive")
@Slf4j
class BudproControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GeneratedNameService generatedNameService;

    private AutoCloseable closeable;

    @BeforeEach
    void before() {
        closeable = MockitoAnnotations.openMocks(this);

        var names = new ArrayList<String>(100);
        for (int i = 0; i < 100; i++) {
            names.add("Personnavn " + i);
        }

        when(generatedNameService.getNames(any(), anyInt()))
                .thenReturn(Flux.empty());
    }

    @AfterEach
    void after() throws Exception {
        closeable.close();
    }

    @Test
    void thatNoSeedGivesDifferentResults() {
        var result1 = webTestClient
                .get().uri("/api/random?limit={limit}", 50)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        var result2 = webTestClient
                .get().uri("/api/random?limit={limit}", 50)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(result1)
                .isNotEqualTo(result2);
    }

    @Test
    void thatSameSeedGivesSameResults() {
        var result1 = webTestClient
                .get().uri("/api/random?seed={seed}&limit={limit}", 123L, 50)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        var result2 = webTestClient
                .get().uri("/api/random?seed={seed}&limit={limit}", 123L, 50)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        assertThat(result1)
                .isEqualTo(result2);
    }

    @Test
    void thatOverrideWorksAsIntended()
            throws Exception {
        var override = new BudproRecord(
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE",
                "OVERRIDE"
        );
        var result = webTestClient
                .post().uri("/api/random?limit={limit}", 3)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(objectMapper.writeValueAsString(override))
                .exchange()
                .expectStatus().isOk()
                .returnResult(BudproRecord.class)
                .getResponseBody()
                .collectList()
                .blockOptional().orElse(List.of())
                .toArray();
        assertThat(result)
                .allMatch(element -> element.equals(override));
    }

}
