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

import no.nav.registre.sdForvalter.database.model.KildeSystemModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.KildeSystemRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.domain.KildeSystem;
import no.nav.registre.sdForvalter.domain.Tps;

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
    private TpsRepository tpsRepository;

    @Autowired
    private KildeSystemRepository kildeSystemRepository;


    @Test
    public void shouldGetTpsSetWithKildeSystem() throws Exception {
        KildeSystemModel altinn = new KildeSystemModel("Altinn");
        kildeSystemRepository.save(altinn);
        TpsModel tpsModel = TpsModel
                .builder()
                .firstName("Test")
                .lastName("Testen")
                .fnr("101010101")
                .kildeSystemModel(altinn)
                .build();
        tpsRepository.save(tpsModel);

        String json = mvc.perform(get("/api/v1/statiskData/tps/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Set<Tps> response = objectMapper.readValue(json, new TypeReference<Set<Tps>>() {
        });
        assertThat(response).containsOnly(new Tps(tpsModel));
    }

    @Test
    public void shouldAddTpsSetToDatabase() throws Exception {
        Tps tps = Tps.builder()
                .firstName("Test")
                .lastName("Testen")
                .fnr("01010101011")
                .build();
        Set<Tps> tpsSet = createTpsSet(tps);
        mvc.perform(post("/api/v1/statiskData/tps/")
                .content(objectMapper.writeValueAsString(tpsSet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(tpsRepository.findAll()).containsOnly(new TpsModel(tps, null));
    }

    @Test
    public void shouldAddKildeSystemToDatabase() throws Exception {
        KildeSystem altinn = new KildeSystem("Altinn");
        final Tps hans = Tps.builder()
                .firstName("Test")
                .lastName("Testen")
                .fnr("01010101011")
                .kildeSystem(altinn)
                .build();

        final Tps petter = Tps.builder()
                .firstName("Testern")
                .lastName("Testernson")
                .fnr("01010101021")
                .kildeSystem(altinn)
                .build();

        final Set<Tps> tpsSet = createTpsSet(hans, petter);
        mvc.perform(post("/api/v1/statiskData/tps/")
                .content(objectMapper.writeValueAsString(tpsSet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(Lists.newArrayList(kildeSystemRepository.findAll()))
                .hasSize(1)
                .first()
                .isEqualToComparingOnlyGivenFields(
                        new KildeSystemModel(altinn),
                        "navn"
                );
    }

    @After
    public void cleanUp() {
        tpsRepository.deleteAll();
        kildeSystemRepository.deleteAll();
    }

    private Set<Tps> createTpsSet(Tps... tpss) {
        return new HashSet<>(Arrays.asList(tpss));
    }
}