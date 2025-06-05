package no.nav.testnav.identpool.providers.v1;

import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.AzureAdTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWebTestClient
//@Import(SecurityTestConfig.class)
@Profile("test")
class LesInnholdComponentTest {

    private static final String IDENT_V1_BASEURL = "/api/v1/identifikator";

    private static final String PERSONIDENTIFIKATOR = "10108000398";
    private static final Identtype IDENTTYPE = Identtype.FNR;
    private static final Rekvireringsstatus REKVIRERINGSSTATUS = Rekvireringsstatus.I_BRUK;
    private static final boolean FINNES_HOS_SKATT = false;
    private static final LocalDate FOEDSELSDATO = LocalDate.of(1980, 10, 10);
    private static final String REKVIRERT_AV = "RekvirererMcRekvirererface";

    @MockitoBean
    private AzureAdTokenService azureAdTokenService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"));

    @DynamicPropertySource
    static void dynamicPropertyRegistry(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", LesInnholdComponentTest::getR2dbcUrl);
        registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername);
        registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword);
    }

    private static String getR2dbcUrl() {
        return postgreSQLContainer.getJdbcUrl().replace("jdbc", "r2dbc");
    }

    @BeforeEach
    void populerDatabaseMedTestidenter() {
        databaseClient.sql("select * from information_schema.columns where table_name = 'personidentifikator'")
                .fetch()
                .all()
                .doOnNext(System.out::println)
                .blockLast();
//        Ident ident = createIdentEntity(Identtype.FNR, PERSONIDENTIFIKATOR, REKVIRERINGSSTATUS, 10);
//        ident.setRekvirertAv(REKVIRERT_AV);
//        identRepository.save(ident)
//                .block();
    }

    @Test
    void skalLeseInnholdIDatabase() throws Exception {

        webTestClient.get()
                .uri(IDENT_V1_BASEURL)
                .header("personidentifikator", PERSONIDENTIFIKATOR)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Ident.class)
                .isEqualTo(createIdentEntity(Identtype.FNR, PERSONIDENTIFIKATOR, Rekvireringsstatus.LEDIG, 10));

//        var resultat = mockMvc.perform(MockMvcRequestBuilders.get(IDENT_V1_BASEURL)
//                        .header("personidentifikator", PERSONIDENTIFIKATOR))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//
//        Ident ident = objectMapper.readValue(resultat.getResponse().getContentAsString(), Ident.class);

//        assertThat(ident, is(notNullValue()));
//        assertThat(ident.getPersonidentifikator(), is(PERSONIDENTIFIKATOR));
//        assertThat(ident.getIdenttype(), is(IDENTTYPE));
//        assertThat(ident.getKjoenn(), is(Kjoenn.MANN));
//        assertThat(ident.getRekvireringsstatus(), is(REKVIRERINGSSTATUS));
//        assertThat(ident.getFoedselsdato(), is(FOEDSELSDATO));
//        assertThat(ident.getRekvirertAv(), is(REKVIRERT_AV));
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
