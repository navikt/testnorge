package no.nav.dolly.integration;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.config.TestOpenSearchConfig;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.BrukerFavoritterRepository;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.TEAM;

@DollySpringBootTest
@Import({TestDatabaseConfig.class, TestOpenSearchConfig.class})
public abstract class AbstractIntegrasjonTest {

    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamBrukerRepository teamBrukerRepository;

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private BrukerFavoritterRepository brukerFavoritterRepository;

    @Autowired
    private TestgruppeRepository testgruppeRepository;

    @Autowired
    private TransaksjonMappingRepository transaksjonMappingRepository;

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Autowired
    private BestillingRepository bestillingRepository;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://localhost:" + TestDatabaseConfig.POSTGRES.getMappedPort(5432) + "/test");
        registry.add("spring.r2dbc.username", TestDatabaseConfig.POSTGRES::getUsername);
        registry.add("spring.r2dbc.password", TestDatabaseConfig.POSTGRES::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @BeforeEach
    void cleanup() {

        teamBrukerRepository.deleteAll().block();
        teamRepository.deleteAll().block();
        identRepository.deleteAll().block();
        brukerFavoritterRepository.deleteAll().block();
        transaksjonMappingRepository.deleteAll().block();
        bestillingProgressRepository.deleteAll().block();
        bestillingKontrollRepository.deleteAll().block();
        bestillingRepository.deleteAll().block();
        testgruppeRepository.deleteAll().block();
        brukerRepository.deleteAll().block();
    }

    Mono<Bruker> createTeamBruker() {

        return brukerRepository.save(
                Bruker
                        .builder()
                        .brukerId(UUID.randomUUID().toString())
                        .brukertype(TEAM)
                        .brukernavn("bruker for team")
                        .build());
    }

    Mono<Bruker> saveBruker(Bruker bruker) {
        return brukerRepository.save(bruker);
    }

    Mono<Team> createTeam(String navn, Bruker brukerForTeam, Bruker personligBruker) {

        return teamRepository.save(
                Team.builder()
                        .navn(navn)
                        .brukerId(brukerForTeam.getId())
                        .beskrivelse("Testing")
                        .opprettetAvId(personligBruker.getId())
                        .opprettetAv(personligBruker)
                        .opprettetTidspunkt(now())
                        .build());
    }

    Mono<TeamBruker> createTeamBruker(Bruker bruker, Team team) {

        return teamBrukerRepository.save(
                TeamBruker
                        .builder()
                        .teamId(team.getId())
                        .brukerId(bruker.getId())
                        .build());
    }

    Flux<Team> getAllTeams() {

        return teamRepository.findAll();
    }
}