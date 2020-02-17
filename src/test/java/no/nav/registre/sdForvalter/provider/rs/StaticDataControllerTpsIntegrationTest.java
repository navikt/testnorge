package no.nav.registre.sdForvalter.provider.rs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import no.nav.registre.sdForvalter.database.model.KildeModel;
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.KildeRepository;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.domain.Kilde;
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
    private KildeRepository kildeRepository;


    @Test
    public void shouldGetTpsSetWithKilde() throws Exception {

        KildeModel altinn = new KildeModel("Altinn");
        kildeRepository.save(altinn);
        TpsModel tpsModel = TpsModel
                .builder()
                .firstName("Hans")
                .lastName("Hansen")
                .fnr("101010101")
                .kildeModel(altinn)
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
        Tps tps = Tps.builder().firstName("Hans").lastName("hansen").fnr("01010101011").build();
        Set<Tps> tpsSet = createTpsSet(tps);
        mvc.perform(post("/api/v1/statiskData/tps/")
                .content(objectMapper.writeValueAsString(tpsSet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(tpsRepository.findAll()).containsOnly(new TpsModel(tps, null));
    }

    @Test
    public void shouldAddKildeToDatabase() throws Exception {
        final Tps hans = Tps.builder().firstName("Hans").lastName("hansen").fnr("01010101011")
                .kilde(new Kilde("Altinn")).build();
        final Tps petter = Tps.builder().firstName("petter").lastName("pettersen").fnr("01010101021")
                .kilde(new Kilde("Altinn")).build();


        final Set<Tps> tpsSet = createTpsSet(hans, petter);
        mvc.perform(post("/api/v1/statiskData/tps/")
                .content(objectMapper.writeValueAsString(tpsSet))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        final KildeModel altinnKilde = new KildeModel(1L, "Altinn");
        assertThat(kildeRepository.findAll())
                .containsOnly(altinnKilde);

        assertThat(tpsRepository.findAll())
                .containsOnly(
                        new TpsModel(hans, altinnKilde),
                        new TpsModel(petter, altinnKilde)
                );
    }

    @After
    public void cleanUp() {
        tpsRepository.deleteAll();
        kildeRepository.deleteAll();
    }


    private Set<Tps> createTpsSet(Tps... tpss) {
        return new HashSet<>(Arrays.asList(tpss));
    }

}