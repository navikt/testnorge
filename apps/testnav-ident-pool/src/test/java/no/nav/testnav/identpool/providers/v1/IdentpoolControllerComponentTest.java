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
import java.util.List;
import java.util.Set;

import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@Testcontainers
@DollySpringBootTest
class IdentpoolControllerComponentTest {

    private static final String IDENT_V1_BASEURL = "/api/v1/identifikator";
    private static final String PROD_SJEKK = "/prodSjekk";
    private static final String FRIGJOER = "/frigjoer";
    private static final String LEDIGE = "/ledige";
    private static final String LEDIG = "/ledig";
    private static final String BRUK = "/bruk";

    private static final String FNR_LEDIG = "10108000398";
    private static final String DNR_LEDIG = "50108000381";
    private static final String FNR_IBRUK = "11108000327";

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private IdentRepository identRepository;

    @MockitoBean
    private TpsMessagingConsumer tpsMessagingConsumer;

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

    @BeforeEach
    void populerDatabaseMedTestidenter() {

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
                .collectList()
                .block();
    }

    @Test
    void lesFraDatabase_LEDIG() {

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
    void lesFraDatabase_IBRUK() {

        webTestClient.get()
                .uri(IDENT_V1_BASEURL)
                .header("personidentifikator", FNR_IBRUK)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Ident.class)
                .isEqualTo(createIdentEntity(Identtype.FNR, FNR_IBRUK, Rekvireringsstatus.I_BRUK, 11));
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
    void hentFlereIdenterEnnDetErMuligAaLevere() {

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
    void prodsjekkIdent_IBRUK() {

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(FNR_IBRUK).inUse(true).build()));

        webTestClient.get()
                .uri(uriSpec -> uriSpec.path(IDENT_V1_BASEURL + PROD_SJEKK)
                        .queryParam("identer", Set.of(FNR_IBRUK))
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[{\"ident\":\"" + FNR_IBRUK + "\",\"inUse\":true,\"available\":false}]");
    }

    @Test
    void prodsjekkIdent_LEDIG() {

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(FNR_LEDIG).inUse(false).build()));

        webTestClient.get()
                .uri(uriSpec -> uriSpec.path(IDENT_V1_BASEURL + PROD_SJEKK)
                        .queryParam("identer", Set.of(FNR_LEDIG))
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[{\"ident\":\"" + FNR_LEDIG + "\",\"inUse\":false,\"available\":true}]");
    }

    @Test
    void frigjoerIdent_OK() {

        identRepository.findByPersonidentifikator(FNR_IBRUK)
                .as(StepVerifier::create)
                .assertNext(ident -> assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK)))
                .verifyComplete();

        webTestClient.post()
                .uri(uriSpec -> uriSpec.path(IDENT_V1_BASEURL + FRIGJOER)
                        .build())
                .bodyValue(List.of(FNR_IBRUK))
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[\"" + FNR_IBRUK + "\"]");

        StepVerifier.create(identRepository.findByPersonidentifikator(FNR_IBRUK))
                .assertNext(ident -> assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.LEDIG)))
                .verifyComplete();
    }

    @Test
    void sjekkLedigeMellomAar_OK() {

        webTestClient.get()
                .uri(uriSpec -> uriSpec.path(IDENT_V1_BASEURL + LEDIGE)
                        .queryParam("fromYear", "1980")
                        .queryParam("toYear", "1981")
                        .queryParam("syntetisk", "false")
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .json("[\"" + FNR_LEDIG + "\"]");
    }

    @Test
    void sjekkLedigSyntetisk_FinnesIDatabaseOgIbruk() {

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(FNR_IBRUK).inUse(true).build()));

        webTestClient.get()
                .uri(uriSpec -> uriSpec.path(IDENT_V1_BASEURL + LEDIG)
                        .build())
                .header("personidentifikator", FNR_IBRUK)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    void sjekkLedigSyntetisk_FinnesIDatabaseOgLedigMenIBrukIProd() {

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet())).thenReturn(Flux.just(
                TpsStatusDTO.builder().ident(FNR_LEDIG).inUse(true).build()));

        webTestClient.get()
                .uri(uriSpec -> uriSpec.path(IDENT_V1_BASEURL + LEDIG)
                        .build())
                .header("personidentifikator", FNR_LEDIG)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Boolean.class)
                .isEqualTo(false);
    }

    @Test
    void bruk_FinnesIkkeIDatabaseErLedig() {

        var IDENT = "22476902081";
        identRepository.findByPersonidentifikator(IDENT)
                .as(StepVerifier::create)
                .expectNextCount(0)
                .verifyComplete();

        webTestClient.post()
                .uri(IDENT_V1_BASEURL + BRUK)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"personidentifikator\":\"" + IDENT + "\"," +
                        "\"bruker\":\"test\"}")
                .exchange()
                .expectStatus()
                .isOk();

        identRepository.findByPersonidentifikator(IDENT)
                .as(StepVerifier::create)
                .assertNext(ident -> {
                    assertThat(ident.getRekvireringsstatus(), is(Rekvireringsstatus.I_BRUK));
                    assertThat(ident.getRekvirertAv(), is("test"));
                })
                .verifyComplete();
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
