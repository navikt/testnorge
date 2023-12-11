package no.nav.registre.sdforvalter.provider.rs;

import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.model.OpprinnelseModel;
import no.nav.registre.sdforvalter.database.repository.EregTagRepository;
import no.nav.registre.sdforvalter.database.repository.GruppeRepository;
import no.nav.registre.sdforvalter.database.repository.OpprinnelseRepository;
import no.nav.registre.sdforvalter.database.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.yml"
)
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GruppeRepository gruppeRepository;
    @Autowired
    private OpprinnelseRepository opprinnelseRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private EregTagRepository eregTagRepository;

    @BeforeEach
    public void setup() {
        GruppeModel gruppeModel = new GruppeModel(null, "Gruppen", "Gruppenbeskrivelse");
        gruppeRepository.save(gruppeModel);

        OpprinnelseModel opprinnelseModel = new OpprinnelseModel(null, "Test");
        opprinnelseRepository.save(opprinnelseModel);
    }

    @AfterEach
    public void cleanUp() {
        reset();
        eregTagRepository.deleteAll();
        tagRepository.deleteAll();
        opprinnelseRepository.deleteAll();
        gruppeRepository.deleteAll();
    }
}
