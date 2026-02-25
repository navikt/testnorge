package no.nav.registre.sdforvalter.provider.rs.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.model.KrrModel;
import no.nav.registre.sdforvalter.database.repository.KrrRepository;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.registre.sdforvalter.domain.KrrListe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DollySpringBootTest
@AutoConfigureMockMvc()
@AutoConfigureWireMock(port = 0)
//@Import(JwtDecoderConfig.class)
class StaticDataControllerV1KrrIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KrrRepository repository;

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

    @AfterEach
    void cleanUp() {
        reset();
        repository.deleteAll();
    }

    @Test
    void shouldGetKrr() throws Exception {
        KrrModel model = createKrrModel("0101011236");
        repository.save(model);
        String json = mvc.perform(get("/api/v1/faste-data/krr")
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        KrrListe response = objectMapper.readValue(json, KrrListe.class);
        assertThat(response.getListe()).containsOnly(new Krr(model));
    }

    @Test
    void shouldCreateKrr() throws Exception {
        Krr krr = createKrr("0101011236");
        mvc.perform(post("/api/v1/faste-data/krr")
                        .content(objectMapper.writeValueAsString(createKrrListe(krr)))
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk());

        assertThat(repository.findAll()).containsOnly(new KrrModel(krr, null, null));
    }

    @Test
    void shouldOnlyGetKrrWithGruppe() throws Exception {
        Krr krr = createKrr("0101011236");
        Krr krrGruppeDolly = createKrr("0101011236", "DOLLY");

        mvc.perform(post("/api/v1/faste-data/krr")
                        .param("gruppe", "DOLLY")
                        .content(objectMapper.writeValueAsString(createKrrListe(krr, krrGruppeDolly)))
                        .contentType(MediaType.APPLICATION_JSON).with(jwt()))
                .andExpect(status().isOk());

        assertThat(repository.findAll()).containsOnly(new KrrModel(krrGruppeDolly, null, null));
    }

}
