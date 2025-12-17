package no.nav.registre.sdforvalter.provider.rs.v1;


import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.sdforvalter.TestSecurityConfig;
import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.registre.sdforvalter.domain.Aareg;
import no.nav.registre.sdforvalter.domain.AaregListe;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@Import(TestSecurityConfig.class)
class StaticDataControllerV1AaregIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AaregRepository repository;

    private AaregModel createAaregModel(String fnr, String orgnr) {
        AaregModel model = new AaregModel();
        model.setFnr(fnr);
        model.setOrgId(orgnr);
        return model;
    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void shouldGetAareg() {
        AaregModel model = createAaregModel("0101011236", "987654321");
        repository.save(model);
        
        webTestClient
                .get()
                .uri("/api/v1/faste-data/aareg")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AaregListe.class)
                .value(response -> assertThat(response.getListe()).containsOnly(new Aareg(model)));
    }

}
