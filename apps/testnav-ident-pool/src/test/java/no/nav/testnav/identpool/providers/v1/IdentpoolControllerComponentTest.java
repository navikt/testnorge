package no.nav.testnav.identpool.providers.v1;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import no.nav.testnav.identpool.ComponentTestbase;
import no.nav.testnav.identpool.IdentPoolApplicationStarter;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@DataR2dbcTest(properties = {"webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT",
        "classes=IdentPoolApplicationStarter.class"})
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(initializers = NaisEnvironmentApplicationContextInitializer.class)
class IdentpoolControllerComponentTest extends ComponentTestbase {

    private static final String API_V1_IDENT_IBRUK = IDENT_V1_BASEURL + "/bruk";
    private static final String API_V1_IDENT_LEDIG = IDENT_V1_BASEURL + "/ledig";

    private static final String FNR_LEDIG = "10108000398";
    private static final String DNR_LEDIG = "50108000381";
    private static final String FNR_IBRUK = "11108000327";
    private static final String NYTT_FNR_LEDIG = "20018049946";

    @BeforeEach
    void populerDatabaseMedTestidenter() {

        // Clear the repository before populating it with test data
//        identRepository.deleteAll()
//                .block();
//        identRepository.saveAll(Arrays.asList(
//                        createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10),
//                        createIdentEntity(Identtype.DNR, DNR_LEDIG, Rekvireringsstatus.LEDIG, 20),
//                        createIdentEntity(Identtype.FNR, FNR_IBRUK, Rekvireringsstatus.I_BRUK, 11),
//                        createIdentEntity(Identtype.DNR, "12108000366", Rekvireringsstatus.I_BRUK, 12)))
//                .collectList()
//                .block();
    }

    @AfterEach
    void clearDatabase() {
//        identRepository.deleteAll()
//                .block();
    }

    @Test
    void lesInnhold() {

        var webClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:8080")
                .build();

        webClient.get()
                .uri(IDENT_V1_BASEURL)
                .header("personidentifikator", FNR_LEDIG)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Ident.class)
                .isEqualTo(createIdentEntity(Identtype.FNR, FNR_LEDIG, Rekvireringsstatus.LEDIG, 10));
    }


    @Test
    void hentLedigFnr() throws Exception {

//        String request = "{\"antall\":\"1\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\" }";
//
//        var webClient = WebTestClient.bindToServer()
//                .baseUrl("http://localhost:8080")
//                .build();
//
//        var result = webClient.get()
//                .uri(API_V1_IDENT_LEDIG)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(request)
//                .exchange()
//                .expectStatus()
//                .isOk()
//                .expectBody()
//
//        var identer = (List<String>) objectMapper.readValue(result.getResponse().getContentAsString(), List.class);
//
//        assertThat(PersonidentUtil.getIdentType(identer.get(0)), is(Identtype.FNR));
//        assertThat(identer, hasSize(1));
    }

    @Test
    void hentLedigDnr() throws Exception {

        String request = "{\"antall\":\"2\", \"identtype\":\"DNR\",\"foedtEtter\":\"1900-01-01\" }";

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident("64038000169").build(),
                TpsStatusDTO.builder().ident("53061600147").build()));

//        var result = mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

//        var identer = (List<String>) objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

//        assertThat(PersonidentUtil.getIdentType(identer.get(0)), is(Identtype.DNR));
//        assertThat(PersonidentUtil.getIdentType(identer.get(1)), is(Identtype.DNR));
//        assertThat(identer, hasSize(2));
    }

    @Test
    void hentLedigIdent() throws Exception {

        String request = "{\"antall\":\"3\", \"identtype\":\"FNR\",\"foedtEtter\":\"1900-01-01\",\"foedtFoer\":\"1950-01-01\"}";

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident("15103300123").build(),
                TpsStatusDTO.builder().ident("16022400197").build(),
                TpsStatusDTO.builder().ident("09021000121").build()));

//        var result = mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();

//        var identer = (List<String>) objectMapper.readValue(result.getResponse().getContentAsString(), List.class);

//        assertThat(identer, hasSize(3));

//        StepVerifier.create(identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
//                        LocalDate.of(1900, 1, 1),
//                        LocalDate.of(1950, 1, 1),
//                        Identtype.FNR,
//                        Rekvireringsstatus.I_BRUK,
//                        false))
//                .expectNextCount(3)
//                .verifyComplete();
    }

    @Test
    void hentForMangeIdenterSomIkkeFinnesIDatabasen() throws Exception {

        String request = "{\"antall\":\"200\", \"foedtEtter\":\"1900-01-01\",\"identtype\":\"FNR\"}";

        when(tpsMessagingConsumer.getIdenterStatuser(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident("15103300123").build()));

//        mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andReturn();
    }

    @Test
    void skalFeileNaarUgyldigIdenttypeBrukes() throws Exception {

        String request = "{\"antall\":\"1\", \"identtype\":\"buksestoerrelse\" }";

//        mockMvc.perform(MockMvcRequestBuilders.post(IDENT_V1_BASEURL)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(request))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andReturn();
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

        assertThat(identRepository.findByPersonidentifikator(NYTT_FNR_LEDIG), is(nullValue()));

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
}
