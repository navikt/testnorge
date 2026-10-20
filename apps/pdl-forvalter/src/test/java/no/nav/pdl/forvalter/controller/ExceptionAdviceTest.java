package no.nav.pdl.forvalter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

class ExceptionAdviceTest {

    private final WebTestClient webTestClient = WebTestClient
            .bindToController(new ConflictController())
            .controllerAdvice(new ExceptionAdvice())
            .build();

    @Test
    void shouldReturnConflictForOptimisticLockingFailure() {

        webTestClient.get()
                .uri("/conflict")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Conflict")
                .jsonPath("$.message")
                .isEqualTo("Personen ble endret av en annen operasjon. Forsøk på nytt.")
                .jsonPath("$.path").isEqualTo("/conflict");
    }

    @RestController
    private static class ConflictController {

        @GetMapping("/conflict")
        Mono<Void> conflict() {
            return Mono.error(new OptimisticLockingFailureException("stale"));
        }
    }
}
