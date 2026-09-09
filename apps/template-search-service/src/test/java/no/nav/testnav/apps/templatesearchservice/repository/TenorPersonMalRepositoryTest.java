package no.nav.testnav.apps.templatesearchservice.repository;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DollySpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TenorPersonMalRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TenorPersonMalRepository malRepository;

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add(
                "spring.flyway.placeholders.malNameIndexExpression",
                () -> "lower(mal_navn)");
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
                .block();
    }

    @Test
    void shouldFindTemplateNameCaseInsensitively() {
        malRepository.save(template("Min mal", "azure-id", TenorMalBrukerType.AZURE)).block();

        StepVerifier.create(malRepository.findByBrukerIdAndMalNavnIgnoreCase(
                        "azure-id",
                        "MIN MAL"))
                .assertNext(mal -> assertThat(mal.getMalNavn()).isEqualTo("Min mal"))
                .verifyComplete();
    }

    @Test
    void shouldEnforceCaseInsensitiveUniqueNamePerOwner() {
        malRepository.save(template("Min mal", "azure-id", TenorMalBrukerType.AZURE)).block();

        StepVerifier.create(malRepository.save(template("MIN MAL", "azure-id", TenorMalBrukerType.AZURE)))
                .expectError(DataIntegrityViolationException.class)
                .verify();
    }

    @Test
    void shouldScopeMutationsToOwner() {
        var alpha = malRepository.save(template("Alpha", "owner", TenorMalBrukerType.AZURE)).block();

        StepVerifier.create(malRepository.findByIdAndBrukerId(alpha.getId(), "other-owner"))
                .verifyComplete();
        StepVerifier.create(malRepository.deleteByIdAndBrukerId(alpha.getId(), "other-owner"))
                .expectNext(0L)
                .verifyComplete();
        StepVerifier.create(malRepository.findByIdAndBrukerId(alpha.getId(), "owner"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldFilterTemplatesByOwnerTypeAndOrganizationUsers() {
        malRepository.save(template("Azure", "azure-id", TenorMalBrukerType.AZURE)).block();
        malRepository.save(template("BankID 1", "bankid-1", TenorMalBrukerType.BANKID)).block();
        malRepository.save(template("BankID 2", "bankid-2", TenorMalBrukerType.BANKID)).block();

        StepVerifier.create(malRepository.findByBrukertype(TenorMalBrukerType.AZURE))
                .assertNext(mal -> assertThat(mal.getBrukerId()).isEqualTo("azure-id"))
                .verifyComplete();
        StepVerifier.create(malRepository.findByBrukertypeAndBrukerIdIn(
                        TenorMalBrukerType.BANKID,
                        List.of("bankid-1")))
                .assertNext(mal -> assertThat(mal.getBrukerId()).isEqualTo("bankid-1"))
                .verifyComplete();
    }

    private static TenorPersonMal template(
            String malNavn,
            String brukerId,
            TenorMalBrukerType brukertype
    ) {
        var now = Instant.now();
        return TenorPersonMal.builder()
                .malNavn(malNavn)
                .soekKriterier("{}")
                .brukerId(brukerId)
                .brukernavn("Testbruker")
                .brukertype(brukertype)
                .opprettet(now)
                .sistOppdatert(now)
                .build();
    }
}
