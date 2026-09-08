package no.nav.testnav.apps.tenorsearchservice.repository;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TenorMalRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TenorMalRepository malRepository;

    @Autowired
    private TenorMalBrukerRepository brukerRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://%s:%d/%s".formatted(
                POSTGRES.getHost(),
                POSTGRES.getFirstMappedPort(),
                POSTGRES.getDatabaseName()));
        registry.add("spring.r2dbc.username", POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", POSTGRES::getPassword);
    }

    @BeforeEach
    void cleanDatabase() {
        malRepository.deleteAll()
                .then(brukerRepository.deleteAll())
                .block();
    }

    @Test
    void shouldAtomicallyCreateAndUpdateTemplate() {
        var bruker = brukerRepository.upsert("azure-id", "Testbruker", "AZURE").block();

        var created = malRepository.upsert(
                        "Min mal",
                        "min mal",
                        "PERSON",
                        "{\"personstatus\":\"BOSATT\"}",
                        bruker.getId())
                .block();
        var updated = malRepository.upsert(
                        "Min mal",
                        "min mal",
                        "ORGANISASJON",
                        "{\"organisasjonsform\":\"AS\"}",
                        bruker.getId())
                .block();

        assertThat(created.ny()).isTrue();
        assertThat(updated.ny()).isFalse();
        assertThat(updated.id()).isEqualTo(created.id());
        assertThat(updated.opprettet()).isEqualTo(created.opprettet());
        assertThat(updated.malType()).isEqualTo(TenorMalType.ORGANISASJON);
        assertThat(updated.soekKriterier()).isEqualTo("{\"organisasjonsform\":\"AS\"}");
    }

    @Test
    void shouldHandleConcurrentUpserts() {
        var users = Flux.range(0, 10)
                .flatMap(_ -> brukerRepository.upsert("shared-user", "Testbruker", "AZURE"))
                .collectList()
                .block();
        var userId = users.getFirst().getId();

        var templates = Flux.range(0, 10)
                .flatMap(index -> malRepository.upsert(
                        "Delt mal",
                        "delt mal",
                        "PERSON",
                        "{\"side\":" + index + "}",
                        userId))
                .collectList()
                .block();

        assertThat(users).extracting("id").containsOnly(userId);
        assertThat(templates).extracting("id").containsOnly(templates.getFirst().id());
        assertThat(templates).filteredOn(result -> result.ny()).hasSize(1);
    }

    @Test
    void shouldSortCaseInsensitivelyAndScopeMutationsToOwner() {
        var owner = brukerRepository.upsert("owner", "Eier", "AZURE").block();
        var otherOwner = brukerRepository.upsert("other-owner", "Annen", "AZURE").block();
        var zeta = malRepository.upsert("zeta", "zeta", "PERSON", "{}", owner.getId()).block();
        var alpha = malRepository.upsert("Alpha", "alpha", "PERSON", "{}", owner.getId()).block();

        StepVerifier.create(malRepository.findByBrukerIdInOrderByMalNavnAscIdAsc(List.of(owner.getId())))
                .assertNext(mal -> assertThat(mal.getId()).isEqualTo(alpha.id()))
                .assertNext(mal -> assertThat(mal.getId()).isEqualTo(zeta.id()))
                .verifyComplete();

        StepVerifier.create(malRepository.updateMalNavn(
                        alpha.id(),
                        otherOwner.getId(),
                        "Ikke tillatt",
                        "ikke tillatt"))
                .expectNext(0)
                .verifyComplete();
        StepVerifier.create(malRepository.deleteByIdAndBrukerId(alpha.id(), otherOwner.getId()))
                .expectNext(0)
                .verifyComplete();
        StepVerifier.create(malRepository.findByIdAndBrukerId(alpha.id(), owner.getId()))
                .expectNextCount(1)
                .verifyComplete();
    }
}
