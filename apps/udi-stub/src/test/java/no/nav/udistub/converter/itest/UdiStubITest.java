package no.nav.udistub.converter.itest;

import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.testing.DollyWireMockExtension;
import no.nav.udistub.database.model.Person;
import no.nav.udistub.database.repository.PersonRepository;
import no.nav.udistub.service.dto.UdiPerson;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static no.nav.udistub.converter.DefaultTestData.TEST_DATE;
import static no.nav.udistub.converter.DefaultTestData.TEST_FLYKTNINGSTATUS;
import static no.nav.udistub.converter.DefaultTestData.TEST_INNREISEFORBUD;
import static no.nav.udistub.converter.DefaultTestData.TEST_NAVN;
import static no.nav.udistub.converter.DefaultTestData.TEST_OPPHOLDSTILLATELSE;
import static no.nav.udistub.converter.DefaultTestData.TEST_OPPHOLDS_GRUNNLAG_KATEGORI;
import static no.nav.udistub.converter.DefaultTestData.TEST_PERSON_ALIAS_FNR;
import static no.nav.udistub.converter.DefaultTestData.TEST_PERSON_FNR;
import static no.nav.udistub.converter.DefaultTestData.TEST_ovrigIkkeOppholdsKategori;
import static no.nav.udistub.converter.DefaultTestData.createPersonTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@DollySpringBootTest
@ExtendWith(DollyWireMockExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class UdiStubITest {

    protected static final UdiPerson TESTPERSON_UDI = createPersonTo();

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    @SuppressWarnings("unused")
    private Flyway flyway;

    private static String readFile(String filename)
            throws IOException {
        return Files.readString(
                new ClassPathResource(filename).getFile().toPath(),
                StandardCharsets.UTF_8
        );
    }

    @BeforeEach
    void setUp() throws IOException {

        flyway.migrate();
        stubFor(
                get(urlMatching("/tpsfstuburl/.*"))
                        .withHeader(AUTHORIZATION, matching("test"))
                        .willReturn(
                                aResponse()
                                        .withBody(readFile("tpsfResponse-happy.json"))
                                        .withStatus(200)
                        )
        );

    }

    @BeforeEach
    void mapToTestPerson() {

        Person personEntity = mapperFacade.map(TESTPERSON_UDI, Person.class);
        personEntity.getAliaser().forEach(aliasTo -> aliasTo.setPerson(personEntity));
        personEntity.getOppholdStatus().setPerson(personEntity);
        personEntity.getArbeidsadgang().setPerson(personEntity);

    }

    @Test
    @Transactional
    void shouldOpprettPersonAndStoreInDb() throws Exception {
        mockMvc
                .perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/v1/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(readFile("opprettPersonRequest-happy.json"))
                                .header("Nav-Personident", TEST_PERSON_FNR)
                                .header("Nav-Consumer-Id", "test"))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        var person = personRepository
                .findByIdent(TEST_PERSON_FNR)
                .orElseThrow(() -> new AssertionError("Person %s not found".formatted(TEST_PERSON_FNR)));

        assertThat(person.getFoedselsDato())
                .isEqualTo(TEST_DATE);
        assertThat(person.getIdent())
                .isEqualTo(TEST_PERSON_FNR);
        assertThat(person.getNavn())
                .isNotNull()
                .satisfies(navn -> {
                    assertThat(navn.getFornavn())
                            .isEqualTo(TEST_NAVN.getFornavn());
                    assertThat(navn.getMellomnavn())
                            .isEqualTo(TEST_NAVN.getMellomnavn());
                    assertThat(navn.getEtternavn())
                            .isEqualTo(TEST_NAVN.getEtternavn());
                });
        assertThat(person.getFlyktning())
                .isEqualTo(TEST_FLYKTNINGSTATUS);
        assertThat(person.getHarOppholdsTillatelse())
                .isEqualTo(TEST_OPPHOLDSTILLATELSE);
        assertThat(person.getAliaser())
                .singleElement()
                .satisfies(alias -> assertThat(alias.getFnr())
                        .isEqualTo(TEST_PERSON_ALIAS_FNR));
        assertThat(person.getOppholdStatus())
                .isNotNull()
                .satisfies(
                        oppholdStatus -> assertThat(oppholdStatus.getIkkeOppholdstilatelseIkkeVilkaarIkkeVisum())
                                .isNotNull()
                                .satisfies(ikkeOppholdstillatelseIkkeVisum -> {
                                            assertThat(ikkeOppholdstillatelseIkkeVisum.getAvslagEllerBortfall())
                                                    .isNotNull()
                                                    .satisfies(avslagEllerBortfall ->
                                                            assertThat(avslagEllerBortfall.getAvslagOppholdstillatelseBehandletGrunnlagOvrig())
                                                                    .isEqualTo(TEST_OPPHOLDS_GRUNNLAG_KATEGORI));
                                            assertThat(ikkeOppholdstillatelseIkkeVisum.getOvrigIkkeOppholdsKategoriArsak())
                                                    .isEqualTo(TEST_ovrigIkkeOppholdsKategori);
                                            assertThat(ikkeOppholdstillatelseIkkeVisum.getUtvistMedInnreiseForbud())
                                                    .isNotNull()
                                                    .satisfies(innreiseForbud -> assertThat(innreiseForbud.getInnreiseForbud())
                                                            .isEqualTo(TEST_INNREISEFORBUD));
                                        }
                                )
                );

    }

}
