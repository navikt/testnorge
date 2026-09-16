package no.nav.testnav.apps.templatesearchservice.repository;

import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.apps.templatesearchservice.consumers.DollyBackendConsumer;
import no.nav.testnav.apps.templatesearchservice.domain.OpprettTenorPersonMalRequest;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.exception.DollyBackendUnavailableException;
import no.nav.testnav.apps.templatesearchservice.service.TenorPersonMalService;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedToken;
import no.nav.testnav.libs.reactivesecurity.action.GetUserInfo;
import no.nav.testnav.libs.securitycore.domain.Token;
import no.nav.testnav.libs.securitycore.domain.UserInfoExtended;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class TenorPersonMalRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private TenorPersonMalRepository malRepository;

    @Autowired
    private TenorPersonMalService malService;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private GetAuthenticatedToken getAuthenticatedToken;

    @MockitoBean
    private GetUserInfo getUserInfo;

    @MockitoBean
    private DollyBackendConsumer dollyBackendConsumer;

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
        malRepository.save(template("Team", "team-bruker-id-42", TenorMalBrukerType.TEAM)).block();

        StepVerifier.create(malRepository.findByBrukertype(TenorMalBrukerType.AZURE))
                .assertNext(mal -> assertThat(mal.getBrukerId()).isEqualTo("azure-id"))
                .verifyComplete();
        StepVerifier.create(malRepository.findByBrukertypeAndBrukerIdIn(
                        TenorMalBrukerType.BANKID,
                        List.of("bankid-1")))
                .assertNext(mal -> assertThat(mal.getBrukerId()).isEqualTo("bankid-1"))
                .verifyComplete();
        StepVerifier.create(malRepository.findByBrukertypeAndBrukerIdIn(
                        TenorMalBrukerType.TEAM,
                        List.of("team-bruker-id-42")))
                .assertNext(mal -> assertThat(mal.getBrukerId()).isEqualTo("team-bruker-id-42"))
                .verifyComplete();
    }

    @Test
    void shouldPersistSharedTeamOwnerForDifferentAzureUsers() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getUserInfo.call())
                .thenReturn(Mono.just(azureUser("azure-user-1")))
                .thenReturn(Mono.just(azureUser("azure-user-2")));
        when(dollyBackendConsumer.getRepresentererTeamBrukerId()).thenReturn(Mono.just("team-bruker-id-42"));
        var request = new OpprettTenorPersonMalRequest("Felles mal", jsonMapper.createObjectNode());

        StepVerifier.create(malService.save(request)
                        .then(malService.save(request))
                        .flatMap(result -> malRepository.findById(result.mal().id())))
                .assertNext(mal -> {
                    assertThat(mal.getBrukerId()).isEqualTo("team-bruker-id-42");
                    assertThat(mal.getBrukertype()).isEqualTo(TenorMalBrukerType.TEAM);
                })
                .verifyComplete();
        StepVerifier.create(malRepository.count())
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    void shouldNotPersistPersonalTemplateWhenTeamLookupFails() {
        when(getAuthenticatedToken.call()).thenReturn(Mono.just(Token.builder().clientCredentials(false).build()));
        when(getUserInfo.call()).thenReturn(Mono.just(azureUser("azure-user")));
        when(dollyBackendConsumer.getRepresentererTeamBrukerId()).thenReturn(Mono.error(
                new DollyBackendUnavailableException("Dolly utilgjengelig", new IllegalStateException())));
        var request = new OpprettTenorPersonMalRequest("Min mal", jsonMapper.createObjectNode());

        StepVerifier.create(malService.save(request))
                .expectError(DollyBackendUnavailableException.class)
                .verify();
        StepVerifier.create(malRepository.count())
                .expectNext(0L)
                .verifyComplete();
    }

    @Test
    void shouldDeleteLegacyDevTemplatesOnlyOnceAndPreserveOtherSchemas() {
        var unrelatedTemplate = malRepository.save(template(
                "Mal i annet skjema", "other-owner", TenorMalBrukerType.AZURE)).block();
        var migrationConfiguration = Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .schemas("legacy_dev_templates")
                .placeholders(Map.of("malNameIndexExpression", "lower(mal_navn)"))
                .target("1");
        assertThat(migrationConfiguration.load().migrate().migrationsExecuted).isEqualTo(1);

        var flyway = migrationConfiguration.target(MigrationVersion.LATEST).load();
        var jdbcTemplate = new JdbcTemplate(flyway.getConfiguration().getDataSource());
        jdbcTemplate.update("""
                insert into legacy_dev_templates.tenor_person_mal
                    (mal_navn, soek_kriterier, bruker_id, brukernavn, brukertype)
                values
                    ('Personlig mal', '{}', 'azure-owner', 'Testbruker', 'AZURE'),
                    ('Teammal', '{}', 'team-bruker-id-42', 'Testteam', 'TEAM'),
                    ('Gammel teammal', '{}', 'dolly-backend-dev:team-bruker-id-42', 'Testteam', 'TEAM')
                """);

        assertThat(flyway.migrate().migrationsExecuted).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject(
                "select count(*) from legacy_dev_templates.tenor_person_mal", Long.class)).isZero();

        var newTemplateId = jdbcTemplate.queryForObject("""
                insert into legacy_dev_templates.tenor_person_mal
                    (mal_navn, soek_kriterier, bruker_id, brukernavn, brukertype)
                values ('Personlig mal', '{}', 'azure-owner', 'Testbruker', 'AZURE')
                returning id
                """, Long.class);
        assertThat(newTemplateId).isGreaterThan(3L);
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(jdbcTemplate.queryForObject(
                "select id from legacy_dev_templates.tenor_person_mal", Long.class)).isEqualTo(newTemplateId);

        StepVerifier.create(malRepository.findById(unrelatedTemplate.getId()))
                .assertNext(mal -> {
                    assertThat(mal.getMalNavn()).isEqualTo("Mal i annet skjema");
                    assertThat(mal.getBrukerId()).isEqualTo("other-owner");
                })
                .verifyComplete();
    }

    private static UserInfoExtended azureUser(String userId) {
        return new UserInfoExtended(
                userId, "889640782", "issuer", "Testbruker", "epost", false, List.of());
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
