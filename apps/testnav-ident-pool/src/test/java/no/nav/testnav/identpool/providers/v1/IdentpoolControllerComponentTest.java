package no.nav.testnav.identpool.providers.v1;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.Arrays;

import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@Testcontainers
@DollySpringBootTest
class IdentpoolControllerComponentTest {

    private static final String IDENT_V1_BASEURL = "/api/v1/identifikator";
    private static final String API_V1_IDENT_IBRUK = IDENT_V1_BASEURL + "/bruk";
    private static final String API_V1_IDENT_LEDIG = IDENT_V1_BASEURL + "/ledig";

    private static final String FNR_LEDIG = "10108000398";
    private static final String DNR_LEDIG = "50108000381";
    private static final String FNR_IBRUK = "11108000327";
    private static final String NYTT_FNR_LEDIG = "20018049946";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private IdentRepository identRepository;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16.9"));

    @DynamicPropertySource
    static void dynamicPropertyRegistry(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", IdentpoolControllerComponentTest::getR2dbcUrl);
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }

    private static String getR2dbcUrl() {
        return postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc");
    }

    @MockitoBean
    private TpsMessagingConsumer tpsMessagingConsumer;

    @BeforeEach
    void populerDatabaseMedTestidenter() {

        identRepository.deleteAll()
                .block();

        identRepository.saveAll(Arrays.asList(
                        createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10),
                        createIdentEntity(Identtype.DNR, DNR_LEDIG, Rekvireringsstatus.LEDIG, 20),
                        createIdentEntity(Identtype.FNR, FNR_IBRUK, Rekvireringsstatus.I_BRUK, 11),
                        createIdentEntity(Identtype.DNR, "12108000366", Rekvireringsstatus.I_BRUK, 12)))
                .collectList()
                .block();
    }

    @AfterEach
    void clearDatabase() {
        identRepository.deleteAll()
                .block();
    }

    @Test
    void lesInnhold() {

        webTestClient.get()
                .uri(IDENT_V1_BASEURL)
                .header("personidentifikator", FNR_LEDIG)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Ident.class)
                .isEqualTo(createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10));
    }

    @Test
    void hentLedigFnr() {

        var request = "{\"antall\":\"1\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\" }";

        webTestClient.post()
                .uri(IDENT_V1_BASEURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("['" + FNR_LEDIG + "']");
    }

    @Test
    void hentLedigDnr() {

        var request = "{\"antall\":\"1\", \"identtype\":\"DNR\",\"foedtEtter\":\"1900-01-01\" }";

        webTestClient.post()
                .uri(IDENT_V1_BASEURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("['" + DNR_LEDIG + "']");
    }

    @Test
    void hentFnrSomIkkeErIDatabase() {

        var fnr1 = "64038000169";
        var fnr2 = "53061600147";

        String request = "{\"antall\":\"2\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\" }";

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(fnr1).build(),
                TpsStatusDTO.builder().ident(fnr2).build()));

        webTestClient.post()
                .uri(IDENT_V1_BASEURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[\"" + fnr1 + "\",\"" + fnr2 + "\"]");
    }

    @Test
    void opprettNyeIdenterSjekkSluttstatus() {

        var fnr1 = "15103300123";
        var fnr2 = "16022400197";
        var fnr3 = "09021000121";

        String request = "{\"antall\":\"3\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\",\"foedtFoer\":\"1950-01-01\"}";

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(fnr1).build(),
                TpsStatusDTO.builder().ident(fnr2).build(),
                TpsStatusDTO.builder().ident(fnr3).build()));

        webTestClient.post()
                .uri(IDENT_V1_BASEURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[\"" + fnr1 + "\",\"" + fnr2 + "\",\"" + fnr3 + "\"]");

        identRepository.countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
                        Rekvireringsstatus.I_BRUK,
                        Identtype.FNR,
                        false,
                        LocalDate.of(1900, 1, 1),
                        LocalDate.of(1950, 1, 1))
                .as(StepVerifier::create)
                .expectNext(3)
                .verifyComplete();
    }

    @Test
    void hentFlereIdenterEnnDetErMuligAaLevere() throws Exception {

        var fnr1 = "15103300123";

        var request = "{\"antall\":\"200\",\"foedtFoer\":\"1900-01-01\",\"foedtEtter\":\"1900-01-01\",\"identtype\":\"FNR\"}";

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(fnr1).build()));

        webTestClient.post()
                .uri(IDENT_V1_BASEURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("Identpool finner ikke ledige identer i hht forespørsel: identType FNR, kjønn null, " +
                        "fødtEtter 1900-01-01, fødtFør 1900-01-01, syntetisk false -- forsøk å bestille med andre kriterier.");
    }

    @Test
    void skalFeileNaarUgyldigIdenttypeBrukes() {

        var request = "{\"antall\":\"1\", \"identtype\":\"buksestoerrelse\" }";

        webTestClient.post()
                .uri(IDENT_V1_BASEURL)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.message")
                .isEqualTo("400 BAD_REQUEST \"Failed to read HTTP message\"");
    }

    @Test
    void markerIBrukPaaIdentAlleredeIBruk() throws Exception {

        String request = "{\"personidentifikator\":\"" + FNR_IBRUK + "\", \"bruker\":\"TesterMcTestFace\" }";

//        mockMvc.perform(MockMvcRequestBuilders.post(API_V1_IDENT_IBRUK)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isConflict())
//                .andReturn();
    }

    @Test
    void markerEksisterendeLedigIdentIBruk() throws Exception {

        StepVerifier.create(identRepository.findByPersonidentifikator(FNR_LEDIG))
                .assertNext(ident -> assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG)))
                .verifyComplete();

        String request = "{\"personidentifikator\":\"" + FNR_LEDIG + "\", \"bruker\":\"TesterMcTestFace\" }";

//        mockMvc.perform(MockMvcRequestBuilders.post(API_V1_IDENT_IBRUK)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

        StepVerifier.create(identRepository.findByPersonidentifikator(FNR_LEDIG))
                .assertNext(ident -> assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)))
                .verifyComplete();
    }

    @Test
    void markerNyLedigIdentIBruk() throws Exception {

        identRepository.findByPersonidentifikator(NYTT_FNR_LEDIG)
                .as(StepVerifier::create)
                .assertNext(ident -> assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG)))
                .verifyComplete();

        String request = "{\"personidentifikator\":\"" + NYTT_FNR_LEDIG + "\", \"bruker\":\"TesterMcTestFace\" }";

//        mockMvc.perform(MockMvcRequestBuilders.post(API_V1_IDENT_IBRUK)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

        StepVerifier.create(identRepository.findByPersonidentifikator(NYTT_FNR_LEDIG))
                .assertNext(ident -> assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)))
                .assertNext(ident -> assertThat(ident.getIdenttype(), is(Identtype.FNR)))
                .verifyComplete();
    }

    @Test
    void sjekkOmLedigIdentErLedig() throws Exception {

//        var result = mockMvc.perform(MockMvcRequestBuilders.get(API_V1_IDENT_LEDIG)
//                        .header("personidentifikator", FNR_LEDIG))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

//        assertThat(Boolean.parseBoolean(result.getResponse().getContentAsString()), is(true));
    }

    @Test
    void sjekkOmUledigIdentErLedig() throws Exception {

//        var result = mockMvc.perform(MockMvcRequestBuilders.get(API_V1_IDENT_LEDIG)
//                        .header("personidentifikator", FNR_IBRUK))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

//        assertThat(Boolean.parseBoolean(result.getResponse().getContentAsString()), is(false));
    }

    @Test
    void eksistererIkkeIDbOgLedigITps() throws Exception {

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(NYTT_FNR_LEDIG).build()));

//        var result = mockMvc.perform(MockMvcRequestBuilders.get(API_V1_IDENT_LEDIG)
//                        .header("personidentifikator", NYTT_FNR_LEDIG))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

//        assertThat(Boolean.parseBoolean(result.getResponse().getContentAsString()), is(true));
    }

    @Test
    void lesIdenterTest() throws Exception {

//        var result = mockMvc.perform(MockMvcRequestBuilders.get(IDENT_V1_BASEURL)
//                        .header("personidentifikator", FNR_LEDIG))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

        Ident expected = createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10);
//        assertThat(objectMapper.readValue(result.getResponse().getContentAsString(), Ident.class), is(expected));
    }

    private static Ident createIdentEntity(Identtype identtype, String ident, Rekvireringsstatus rekvireringsstatus, int day) {
        return Ident.builder()
                .identtype(identtype)
                .personidentifikator(ident)
                .rekvireringsstatus(rekvireringsstatus)
                .kjoenn(Kjoenn.MANN)
                .foedselsdato(LocalDate.of(1980, 10, day))
                .syntetisk(isSyntetisk(ident))
                .build();
    }
}
