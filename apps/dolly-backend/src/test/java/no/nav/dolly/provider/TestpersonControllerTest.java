package no.nav.dolly.provider;


import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TestpersonControllerTest extends AbstractControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private PdlDataConsumer pdlDataConsumer;

    @Test
    @DisplayName("Sletter Testident fra Testgruppe")
    void deleteExisting() {

        var bruker = createBruker().block();

        var testgruppe = createTestgruppe("Testgruppe", bruker).block();
        var testident1 = createTestident("Testident 1", testgruppe).block();

        when(pdlDataConsumer.slettPdl(any())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/api/v1/ident/{ident}", testident1.getIdent())
                .exchange()
                .expectStatus()
                .isOk();

        assertThat(findTestident(testident1.getIdent()).block())
                .isNull();
    }

    @Test
    @DisplayName("Kan ikke slette ikke-eksisterende Testident")
    void cannotDeleteNonExisting() {

        var id = new Random().nextLong(100, Long.MAX_VALUE);

        webTestClient
                .delete()
                .uri("/api/v1/ident/{ident}", id)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.message")
                .value(message -> assertThat(message).asString().contains("Testperson med ident " + id + " ble ikke funnet"));
    }
}