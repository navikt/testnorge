package no.nav.registre.sdForvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import no.nav.registre.sdForvalter.database.model.GruppeModel;
import no.nav.registre.sdForvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdForvalter.database.model.TpsIdentModel;
import no.nav.registre.sdForvalter.database.repository.GruppeRepository;
import no.nav.registre.sdForvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdForvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdForvalter.domain.TpsIdent;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    TpsIdenterRepository tpsIdenterRepository;
    @Autowired
    GruppeRepository gruppeRepository;
    @Autowired
    OpprinnelseRepository opprinnelseRepository;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.webApplicationContext)
                .build();
        GruppeModel gruppeModel = new GruppeModel(null, "Gruppen", "Gruppenbeskrivelse");
        gruppeRepository.save(gruppeModel);

        OpprinnelseModel opprinnelseModel = new OpprinnelseModel(null, "Test");
        opprinnelseRepository.save(opprinnelseModel);
    }

    @Test
    public void should_save_one_ident_from_csv_to_tps_ident_database() throws Exception {
        String csvInnhold = "FNR;Fornavn;Etternavn;Adresse;Postnummer;Poststed;Gruppe;Opprinnelse\n" +
                "12345678912;Dolly;Dollesen;Dollygata 2;9999;Dollyville;Gruppen;Test";

        TpsIdent expectedTpsPerson = TpsIdent.builder()
                .fnr("12345678912")
                .firstName("Dolly")
                .lastName("Dollesen")
                .address("Dollygata 2")
                .postNr("9999")
                .city("Dollyville")
                .build();
        List<TpsIdentModel> expectedTpsIdenterListe = List.of(new TpsIdentModel(
                        expectedTpsPerson,
                        new OpprinnelseModel(null, "Test"),
                        new GruppeModel(null, "Gruppen", "Gruppenbeskrivelse")
                )
        );

        assertListOfPersonsFromCsvIsSavedInDatabase(expectedTpsIdenterListe, csvInnhold);
    }

    @Test
    public void should_save_two_idents_from_csv_to_tps_ident_database() throws Exception {
        String csvInnhold = "FNR;Fornavn;Etternavn;Adresse;Postnummer;Poststed;Gruppe;Opprinnelse\n" +
                "12345678910;Dolly;Dollesen;Dollygata 2;9999;Dollyville;Gruppen;Test\n" +
                "12345678911;Donald;Dollesen;Dollygata 3;2222;Dollyby;Gruppen;Test";

        TpsIdent expectedTpsPerson1 = TpsIdent.builder()
                .fnr("12345678910")
                .firstName("Dolly")
                .lastName("Dollesen")
                .address("Dollygata 2")
                .postNr("9999")
                .city("Dollyville")
                .build();
        TpsIdent expectedTpsPerson2 = TpsIdent.builder()
                .fnr("12345678911")
                .firstName("Donald")
                .lastName("Dollesen")
                .address("Dollygata 3")
                .postNr("2222")
                .city("Dollyby")
                .build();
        List<TpsIdentModel> expectedTpsIdenterListe = List.of(
                new TpsIdentModel(expectedTpsPerson1, new OpprinnelseModel(null, "Test"), new GruppeModel(null, "Gruppen", "Gruppenbeskrivelse")),
                new TpsIdentModel(expectedTpsPerson2, new OpprinnelseModel(null, "Test"), new GruppeModel(null, "Gruppen", "Gruppenbeskrivelse"))
        );

        assertListOfPersonsFromCsvIsSavedInDatabase(expectedTpsIdenterListe, csvInnhold);
    }

    public void assertListOfPersonsFromCsvIsSavedInDatabase(List<TpsIdentModel> expectedTpsIdenterListe, String csvInnhold) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart("/api/v1/faste-data/file/tpsIdenter/")
                .file("file", csvInnhold.getBytes())
                .characterEncoding("UTF_8");

        mvc.perform(builder).andExpect(status().isOk());

        assertThat(tpsIdenterRepository.findAll())
                .containsAll(expectedTpsIdenterListe);
    }

    @After
    public void cleanUp() {
        reset();
        tpsIdenterRepository.deleteAll();
        opprinnelseRepository.deleteAll();
        gruppeRepository.deleteAll();
    }
}
