package no.nav.testnav.identpool.providers.v1;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.identpool.config.AbstractTestcontainer;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.BatchStatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.AjourholdRepository;
import no.nav.testnav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;

@DollySpringBootTest
class AjourholdComponentTest extends AbstractTestcontainer {

    private static final String BATCH_V1_BASEURL = "/api/v1/batch";
    private static final String START_PRODCLEAN = "/start-prod-clean";
    private static final String START_MINING = "/start-mining";

    private static final String DB_FILE = "db/db-default-config.sql";
    private static final String PROD_CLEAN_STATUS = "Oppdatert 12 identer som var allokert for prod.";
    private static final String MINING_STATUS = "Allokert nye identer for: år 1950 antall 4380";

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AjourholdRepository ajourholdRepository;

    @MockitoBean
    private TpsMessagingConsumer tpsMessagingConsumer;

    @BeforeEach
    void populerDatabaseMedTestidenter() throws IOException {

        ajourholdRepository.deleteAll()
                .block();

        identRepository.deleteAll()
                .collectList()
                .block();

        var dbFile = readSqlFile();

        databaseClient.sql(dbFile)
                .fetch()
                .all()
                .collectList()
                .block();
    }

    @Order(2)
    @Test
    void startProdClean() {

        when(tpsMessagingConsumer.getIdenterProdStatus(anySet()))
                .thenReturn(getTpsStatus());

        webTestClient.post()
                .uri(uriSpec -> uriSpec.path(BATCH_V1_BASEURL + START_PRODCLEAN)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(PROD_CLEAN_STATUS);

        ajourholdRepository.findAll()
                .as(StepVerifier::create)
                .assertNext(ajourhold -> assertThat(ajourhold.getStatus(), is(BatchStatus.CLEAN_STARTED)))
                .assertNext(ajourhold -> {
                    assertThat(ajourhold.getStatus(), is(BatchStatus.CLEAN_COMPLETED));
                    assertThat(ajourhold.getMelding(), is(PROD_CLEAN_STATUS));
                })
                .verifyComplete();

        identRepository.findAll()
                .filter(ident -> "TPS-PROD".equals(ident.getRekvirertAv()))
                .count()
                .as(StepVerifier::create)
                .expectNext(12L)
                .verifyComplete();
    }

    @Order(3)
    @Test
    void startMining() {

        webTestClient.mutate()
                .responseTimeout(Duration.ofSeconds(300))
                .build()
                .post()
                .uri(uriSpec -> uriSpec.path(BATCH_V1_BASEURL + START_MINING)
                        .queryParam("yearToGenerate", 1950)
                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo(MINING_STATUS);

        ajourholdRepository.findAll()
                .as(StepVerifier::create)
                .assertNext(ajourhold -> assertThat(ajourhold.getStatus(), is(BatchStatus.MINING_STARTED)))
                .assertNext(ajourhold -> {
                    assertThat(ajourhold.getStatus(), is(BatchStatus.MINING_COMPLETED));
                    assertThat(ajourhold.getMelding(), is(MINING_STATUS));
                })
                .verifyComplete();

        identRepository.findAll()
                .filter(ident -> ident.getFoedselsdato().getYear() == 1950)
                .count()
                .as(StepVerifier::create)
                .expectNext(4380L)
                .verifyComplete();
    }

    private String readSqlFile() throws IOException {

        var resource = new ClassPathResource(DB_FILE);
        var bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        return new String(bytes, StandardCharsets.UTF_8);
    }

    private Flux<TpsStatusDTO> getTpsStatus() {

        var identer = Set.of(
                "14496205422",
                "14496208650",
                "14496201087",
                "14496209444",
                "14496205260",
                "14496209282",
                "14496200889",
                "14496201834",
                "14496202628",
                "14496206801",
                "14496201672",
                "14496204205");

        return Flux.fromIterable(identer)
                .map(ident -> TpsStatusDTO.builder()
                        .ident(ident)
                        .inUse(true)
                        .build());
    }
}
