package no.nav.registre.sdForvalter.provider.rs;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.model.GruppeModel;
import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
import no.nav.registre.sdForvalter.database.repository.GruppeRepository;
import no.nav.registre.sdForvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.sdForvalter.domain.Opprinnelse;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class StaticDataControllerEregIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EregRepository eregRepository;

    @Autowired
    private OpprinnelseRepository opprinnelseRepository;

    @Autowired
    private GruppeRepository gruppeRepository;

    @Test
    public void shouldGetEregsWithOpprinnelse() throws Exception {
        OpprinnelseModel altinn = opprinnelseRepository.save(new OpprinnelseModel("Altinn"));
        EregModel model = EregModel
                .builder()
                .orgnr("123456789")
                .enhetstype("BEDR")
                .opprinnelseModel(altinn)
                .build();

        eregRepository.save(model);

        String json = mvc.perform(get("/api/v1/faste-data/ereg/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(new Ereg(model));
    }


    @Test
    public void shouldAddEregSetToDatabase() throws Exception {
        Ereg ereg = Ereg.builder()
                .orgnr("987654321")
                .enhetstype("BEDR")
                .build();
        mvc.perform(post("/api/v1/faste-data/ereg/")
                .content(objectMapper.writeValueAsString(create(ereg)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(eregRepository.findAll())
                .hasSize(1)
                .first()
                .isEqualToIgnoringGivenFields(
                        new EregModel(ereg, null, null, null),
                        "id", "createdAt", "updatedAt"
                );
    }

    @Test
    public void shouldGetEregWithGruppe() throws Exception {
        GruppeModel gruppeModel = gruppeRepository.save(new GruppeModel(
                null,
                "TestKode",
                "TestBeskrivelse"
        ));
        EregModel eregModel = EregModel
                .builder()
                .orgnr("123456789")
                .enhetstype("BEDR")
                .gruppeModel(gruppeModel)
                .build();
        eregRepository.save(eregModel);

        String json = mvc.perform(get("/api/v1/faste-data/ereg/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(new Ereg(eregModel));
    }


    @Test
    public void shouldOnlyGetEregWithGruppe() throws Exception {
        GruppeModel gruppeModel = gruppeRepository.save(new GruppeModel(
                null,
                "TestKode",
                "TestBeskrivelse"
        ));
        EregModel eregModel = EregModel
                .builder()
                .orgnr("123456789")
                .enhetstype("BEDR")
                .build();

        EregModel eregWithGruppeModel = EregModel
                .builder()
                .orgnr("987654321")
                .enhetstype("BEDR")
                .gruppeModel(gruppeModel)
                .build();

        eregRepository.saveAll(Arrays.asList(eregModel, eregWithGruppeModel));

        String json = mvc.perform(
                get("/api/v1/faste-data/ereg/")
                        .param("gruppe", gruppeModel.getKode())
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EregListe response = objectMapper.readValue(json, EregListe.class);
        assertThat(response.getListe()).containsOnly(new Ereg(eregWithGruppeModel));
    }

    @Test
    public void shouldAddEregWithGrouppe() throws Exception {
        GruppeModel gruppeModel = gruppeRepository.save(new GruppeModel(
                null,
                "TestKode",
                "TestBeskrivelse"
        ));
        Ereg ereg = Ereg.builder()
                .orgnr("987654321")
                .enhetstype("BEDR")
                .gruppe(gruppeModel.getKode())
                .build();

        mvc.perform(post("/api/v1/faste-data/ereg/")
                .content(objectMapper.writeValueAsString(create(ereg)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Iterable<EregModel> iterable = eregRepository.findAll();
        assertThat(iterable).hasSize(1);
        assertThat(iterable.iterator().next().getGruppeModel())
                .isEqualToIgnoringGivenFields(gruppeModel, "id", "updatedAt", "createdAt");
    }


    @Test
    public void shouldAddOpprinnelseToDatabase() throws Exception {
        Opprinnelse altinn = new Opprinnelse("Altinn");
        Ereg ereg_123456789 = Ereg.builder()
                .orgnr("123456789")
                .enhetstype("BEDR")
                .opprinelse(altinn.getNavn())
                .build();
        Ereg ereg_987654321 = Ereg.builder()
                .orgnr("987654321")
                .enhetstype("BEDR")
                .opprinelse(altinn.getNavn())
                .build();
        mvc.perform(post("/api/v1/faste-data/ereg/")
                .content(objectMapper.writeValueAsString(create(ereg_123456789, ereg_987654321)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        assertThat(Lists.newArrayList(opprinnelseRepository.findAll()))
                .hasSize(1)
                .first()
                .isEqualToComparingOnlyGivenFields(new OpprinnelseModel(altinn), "navn");
    }


    private EregListe create(Ereg... eregs) {
        return new EregListe(Arrays.asList(eregs));
    }

    @After
    public void cleanUp() {
        eregRepository.deleteAll();
        gruppeRepository.deleteAll();
        opprinnelseRepository.deleteAll();
    }

}