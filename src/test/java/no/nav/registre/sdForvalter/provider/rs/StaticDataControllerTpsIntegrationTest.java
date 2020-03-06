package no.nav.registre.sdForvalter.provider.rs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.database.model.TpsIdentModel;
import no.nav.registre.sdForvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdForvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdForvalter.domain.Opprinnelse;
import no.nav.registre.sdForvalter.domain.TpsIdent;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class StaticDataControllerTpsIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TpsIdenterRepository tpsIdenterRepository;

    @Autowired
    private OpprinnelseRepository opprinnelseRepository;


    @Test
    public void shouldGetTpsIdentSetWithOpprinnelse() throws Exception {
        OpprinnelseModel altinn = opprinnelseRepository.save(new OpprinnelseModel("Altinn"));

        TpsIdentModel tpsIdentModel = TpsIdentModel
                .builder()
                .firstName("Test")
                .lastName("Testen")
                .fnr("101010101")
                .opprinnelseModel(altinn)
                .build();
        tpsIdenterRepository.save(tpsIdentModel);

        String json = mvc.perform(get("/api/v1/faste-data/tps/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Set<TpsIdent> response = objectMapper.readValue(json, new TypeReference<Set<TpsIdent>>() {
        });
        assertThat(response).containsOnly(new TpsIdent(tpsIdentModel));
    }

    @Test
    public void shouldAddTpsIdentSetToDatabase() throws Exception {
        TpsIdent tpsIdent = TpsIdent.builder()
                .firstName("Test")
                .lastName("Testen")
                .fnr("01010101011")
                .build();
        Set<TpsIdent> tpsIdentSet = createTpsIdentSet(tpsIdent);
        mvc.perform(post("/api/v1/faste-data/tps/")
                .content(objectMapper.writeValueAsString(tpsIdentSet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(tpsIdenterRepository.findAll()).containsOnly(new TpsIdentModel(tpsIdent, null));
    }

    @Test
    public void shouldAddOpprinnelseToDatabase() throws Exception {
        Opprinnelse altinn = new Opprinnelse("Altinn");
        final TpsIdent hans = TpsIdent.builder()
                .firstName("Test")
                .lastName("Testen")
                .fnr("01010101011")
                .opprinelse(altinn.getNavn())
                .build();

        final TpsIdent petter = TpsIdent.builder()
                .firstName("Testern")
                .lastName("Testernson")
                .fnr("01010101021")
                .opprinelse(altinn.getNavn())
                .build();

        final Set<TpsIdent> tpsIdentSet = createTpsIdentSet(hans, petter);
        mvc.perform(post("/api/v1/faste-data/tps/")
                .content(objectMapper.writeValueAsString(tpsIdentSet))
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


    @After
    public void cleanUp() {
        tpsIdenterRepository.deleteAll();
        opprinnelseRepository.deleteAll();
    }

    private Set<TpsIdent> createTpsIdentSet(TpsIdent... tpsIdenter) {
        return new HashSet<>(Arrays.asList(tpsIdenter));
    }
}