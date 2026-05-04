package no.nav.registre.sdforvalter.provider.rs;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.sdforvalter.TestSecurityConfig;
import no.nav.registre.sdforvalter.database.model.KrrModel;
import no.nav.registre.sdforvalter.database.repository.KrrRepository;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;


@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@DirtiesContext
@Import(TestSecurityConfig.class)
class OrkestreringControllerKrrIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KrrRepository repository;

    @AfterEach
    void cleanUp() {
        reset();
        repository.deleteAll();
    }

    @Test
    void shouldInitiateKrrFromDatabase() throws Exception {
        var model = new KrrModel();
        model.setFnr("01010112365");
        repository.save(model);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/kontaktinformasjon")
                .stubPost(201);

        webTestClient
                .post()
                .uri("/api/v1/orkestrering/krr")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/kontaktinformasjon")
                .verifyPost();

    }

}