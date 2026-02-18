package no.nav.registre.sdforvalter.provider.rs.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.model.KrrModel;
import no.nav.registre.sdforvalter.database.repository.KrrRepository;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.registre.sdforvalter.domain.KrrListe;
import no.nav.registre.sdforvalter.JwtDecoderConfig;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
@Import(JwtDecoderConfig.class)
class StaticDataControllerV1KrrIntegrationTest {

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

    private KrrModel createKrrModel(String fnr) {
        return createKrrModel(fnr, null);
    }

    private KrrModel createKrrModel(String fnr, String gruppe) {
        KrrModel model = new KrrModel();
        model.setFnr(fnr);
        model.setGruppeModel(gruppe != null ? createGruppeModel(gruppe) : null);
        return model;
    }

    private GruppeModel createGruppeModel(String gruppe) {
        return new GruppeModel(null, gruppe, null);
    }

    private KrrListe createKrrListe(Krr... krrs) {
        return new KrrListe(Arrays.asList(krrs));
    }

    private Krr createKrr(String fnr) {
        return new Krr(createKrrModel(fnr));
    }

    private Krr createKrr(String fnr, String gruppe) {
        return new Krr(createKrrModel(fnr, gruppe));
    }

    @Test
    void shouldGetKrr() {
        KrrModel model = createKrrModel("0101011236");
        repository.save(model);
        webTestClient.get()
                .uri("/api/v1/faste-data/krr")
                .exchange()
                .expectStatus().isOk()
                .expectBody(KrrListe.class)
                .value(response -> assertThat(response.getListe()).containsOnly(new Krr(model)));
    }

    @Test
    void shouldCreateKrr() {
        Krr krr = createKrr("0101011236");
        webTestClient.post()
                .uri("/api/v1/faste-data/krr")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createKrrListe(krr))
                .exchange()
                .expectStatus().isOk();

        assertThat(repository.findAll()).containsOnly(new KrrModel(krr, null, null));
    }

    @Test
    void shouldOnlyGetKrrWithGruppe() {
        Krr krr = createKrr("0101011236");
        Krr krrGruppeDolly = createKrr("0101011236", "DOLLY");

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/faste-data/krr").queryParam("gruppe", "DOLLY").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createKrrListe(krr, krrGruppeDolly))
                .exchange()
                .expectStatus().isOk();

        assertThat(repository.findAll()).containsOnly(new KrrModel(krrGruppeDolly, null, null));
    }

}
