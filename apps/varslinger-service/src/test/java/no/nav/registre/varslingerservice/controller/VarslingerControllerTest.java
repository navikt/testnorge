package no.nav.registre.varslingerservice.controller;

import no.nav.registre.varslingerservice.SecurityTestConfig;
import no.nav.registre.varslingerservice.repository.VarslingRepository;
import no.nav.registre.varslingerservice.repository.model.VarslingModel;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@DollySpringBootTest
@Import(SecurityTestConfig.class)
class VarslingerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private VarslingRepository varslingRepository;

    @BeforeEach
    void beforeEach() {
        varslingRepository.deleteAll();
    }

    @AfterEach
    void afterEach() {
        varslingRepository.deleteAll();
    }

    @Test
    void testNoWarningsInRepository() {
        webTestClient.get().uri("/api/v1/varslinger")
                .exchange()
                .expectStatus().isOk()
                .expectBody().json("[]");
    }

    @Test
    void testThreeWarningsInRepository() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("2").build());
        var v3 = varslingRepository.save(VarslingModel.builder().varslingId("3").build());

        webTestClient.get().uri("/api/v1/varslinger")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[0].varslingId").isEqualTo(v1.getVarslingId())
                .jsonPath("$[1].varslingId").isEqualTo(v2.getVarslingId())
                .jsonPath("$[2].varslingId").isEqualTo(v3.getVarslingId());
    }

    @Test
    void testPutWarning() {
        varslingRepository.save(VarslingModel.builder().varslingId("1").build());

        webTestClient.put().uri("/api/v1/varslinger")
                .bodyValue("{\"varslingId\":\"1\"}")
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/api/v1/varslinger/1");

        var saved = varslingRepository.findById("1").orElseThrow();
        org.assertj.core.api.Assertions.assertThat(saved.getVarslingId()).isEqualTo("1");
    }

    @Test
    void deleteNonexistingWarning() {
        webTestClient.delete().uri("/api/v1/varslinger/{id}", "some-nonexisting-id")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteExistingWarning() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("1").build());
        var v2 = varslingRepository.save(VarslingModel.builder().varslingId("2").build());

        webTestClient.delete().uri("/api/v1/varslinger/{id}", v1.getVarslingId())
                .exchange()
                .expectStatus().isOk();

        org.assertj.core.api.Assertions.assertThat(varslingRepository.findById(v1.getVarslingId()))
                .isEmpty();
        org.assertj.core.api.Assertions.assertThat(varslingRepository.findById(v2.getVarslingId()))
                .isNotEmpty();
    }

    @Test
    void getNonexistingWarning() {
        webTestClient.get().uri("/api/v1/varslinger/{id}", "some-nonexisting-id")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getSpecificWarning() {
        var v1 = varslingRepository.save(VarslingModel.builder().varslingId("1").build());

        webTestClient.get().uri("/api/v1/varslinger/{id}", v1.getVarslingId())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.varslingId").isEqualTo(v1.getVarslingId());
    }

}
