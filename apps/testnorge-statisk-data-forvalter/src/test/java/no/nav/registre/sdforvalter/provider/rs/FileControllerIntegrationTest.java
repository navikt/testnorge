package no.nav.registre.sdforvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Set;

import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.registre.sdforvalter.database.repository.EregTagRepository;
import no.nav.registre.sdforvalter.database.repository.GruppeRepository;
import no.nav.registre.sdforvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdforvalter.database.repository.TagRepository;
import no.nav.registre.sdforvalter.database.repository.TpsIdentTagRepository;
import no.nav.registre.sdforvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdforvalter.domain.TpsIdent;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private TpsIdenterRepository tpsIdenterRepository;
    @Autowired
    private GruppeRepository gruppeRepository;
    @Autowired
    private OpprinnelseRepository opprinnelseRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private EregTagRepository eregTagRepository;
    @Autowired
    private TpsIdentTagRepository tpsIdentTagRepository;

    @BeforeEach
    public void setup() {
        GruppeModel gruppeModel = new GruppeModel(null, "Gruppen", "Gruppenbeskrivelse");
        gruppeRepository.save(gruppeModel);

        OpprinnelseModel opprinnelseModel = new OpprinnelseModel(null, "Test");
        opprinnelseRepository.save(opprinnelseModel);
    }

    @Test
    public void should_save_one_ident_from_csv_to_tps_ident_database() throws Exception {
        String csvInnhold = "FNR*;Fornavn;Etternavn;Adresse;Postnummer;Poststed;Gruppe;Opprinnelse;Tags\n" +
                "12345678912;Dolly;Dollesen;Dollygata 2;9999;Dollyville;Gruppen;Test;OTP";

        TpsIdent expectedTpsPerson = TpsIdent.builder()
                .fnr("12345678912")
                .firstName("Dolly")
                .lastName("Dollesen")
                .address("Dollygata 2")
                .postNr("9999")
                .city("Dollyville")
                .tags(Set.of("OTP"))
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
        String csvInnhold = "FNR*;Fornavn;Etternavn;Adresse;Postnummer;Poststed;Gruppe;Opprinnelse;Tags\n" +
                "12345678910;Dolly;Dollesen;Dollygata 2;9999;Dollyville;Gruppen;Test;OTP\n" +
                "12345678911;Donald;Dollesen;Dollygata 3;2222;Dollyby;Gruppen;Test;";

        TpsIdent expectedTpsPerson1 = TpsIdent.builder()
                .fnr("12345678910")
                .firstName("Dolly")
                .lastName("Dollesen")
                .address("Dollygata 2")
                .postNr("9999")
                .city("Dollyville")
                .tags(Set.of("OTP"))
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

    @AfterEach
    public void cleanUp() {
        reset();
        eregTagRepository.deleteAll();
        tpsIdentTagRepository.deleteAll();
        tagRepository.deleteAll();
        tpsIdenterRepository.deleteAll();
        opprinnelseRepository.deleteAll();
        gruppeRepository.deleteAll();
    }
}
