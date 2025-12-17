package no.nav.dolly.integration;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.TEAM;

@Disabled
@DollySpringBootTest
@Import(TestDatabaseConfig.class)
public abstract class AbstractIntegrasjonTest {

    @Autowired
    private BrukerRepository brukerRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private TeamBrukerRepository teamBrukerRepository;

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