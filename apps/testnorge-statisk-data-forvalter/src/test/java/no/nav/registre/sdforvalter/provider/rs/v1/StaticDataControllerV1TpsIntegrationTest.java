package no.nav.registre.sdforvalter.provider.rs.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import no.nav.registre.sdforvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.registre.sdforvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdforvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdforvalter.domain.Opprinnelse;
import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.domain.TpsIdentListe;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class StaticDataControllerV1TpsIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TpsIdenterRepository tpsIdenterRepository;

    @Autowired
    private OpprinnelseRepository opprinnelseRepository;

    @Test
    @Disabled
    public void shouldGetTpsIdentSetWithOpprinnelse() throws Exception {
        OpprinnelseModel altinn = opprinnelseRepository.save(new OpprinnelseModel("Altinn"));
        TpsIdentModel tpsIdentModel = createIdentModel("01010101011", "Petter", "Petterson", altinn);
        tpsIdenterRepository.save(tpsIdentModel);

        String json = mvc.perform(get("/api/v1/faste-data/tps?genererManglendeNavn=false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TpsIdentListe response = objectMapper.readValue(json, TpsIdentListe.class);
        assertThat(response.getListe()).containsOnly(new TpsIdent(tpsIdentModel, new ArrayList<>()));
    }

    @Test
    @Disabled
    public void shouldAddTpsIdentSetToDatabase() throws Exception {
        TpsIdent tpsIdent = createIdent("01010101011", "Petter", "Petterson");
        mvc.perform(post("/api/v1/faste-data/tps?genererManglendeNavn=false")
                .content(objectMapper.writeValueAsString(createTpsIdenter(tpsIdent)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(tpsIdenterRepository.findAll()).containsOnly(new TpsIdentModel(tpsIdent, null, null));
    }

    @Test
    @Disabled
    public void shouldAddOpprinnelseToDatabase() throws Exception {
        Opprinnelse altinn = new Opprinnelse("Altinn");
        final TpsIdent hans = createIdent("01010101011", "Hans", "Hansen");
        final TpsIdent petter = createIdent("01010101021", "Petter", "Petterson", altinn);

        mvc.perform(post("/api/v1/faste-data/tps?genererManglendeNavn=false")
                .content(objectMapper.writeValueAsString(createTpsIdenter(hans, petter)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(Lists.newArrayList(opprinnelseRepository.findAll()))
                .hasSize(1)
                .first()
                .isEqualToComparingOnlyGivenFields(
                        new OpprinnelseModel(altinn),
                        "navn"
                );
    }


    @AfterEach
    public void cleanUp() {
        tpsIdenterRepository.deleteAll();
        opprinnelseRepository.deleteAll();
    }


    private TpsIdentModel createIdentModel(String fnr, String firstName, String lastName, OpprinnelseModel opprinnelseModel) {
        TpsIdentModel model = new TpsIdentModel();
        model.setFnr(fnr);
        model.setFirstName(firstName);
        model.setLastName(lastName);
        model.setOpprinnelseModel(opprinnelseModel);
        return model;
    }

    private TpsIdentModel createIdentModel(String fnr, String firstName, String lastName) {
        return createIdentModel(fnr, firstName, lastName, null);
    }

    private TpsIdent createIdent(String fnr, String firstName, String lastName) {
        return new TpsIdent(createIdentModel(fnr, firstName, lastName), new ArrayList<>());
    }

    private TpsIdent createIdent(String fnr, String firstName, String lastName, Opprinnelse opprinnelse) {
        return new TpsIdent(createIdentModel(fnr, firstName, lastName, new OpprinnelseModel(opprinnelse)), new ArrayList<>());
    }

    private TpsIdentListe createTpsIdenter(TpsIdent... tpsIdenter) {
        return new TpsIdentListe(Arrays.asList(tpsIdenter));
    }
}