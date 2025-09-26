package no.nav.dolly.integration;

import no.nav.dolly.config.TestDatabaseConfig;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.jpa.TeamBruker;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.dolly.repository.BrukerRepository;
import no.nav.dolly.repository.TeamBrukerRepository;
import no.nav.dolly.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.time.LocalDateTime.now;
import static no.nav.dolly.domain.jpa.Bruker.Brukertype.TEAM;
import static no.nav.dolly.util.DateZoneUtil.CET;

@Disabled
@DollySpringBootTest
@ExtendWith(TestDatabaseConfig.class)
public abstract class AbstractIntegrasjonTest {

    @Autowired
    private BrukerRepository brukerRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamBrukerRepository teamBrukerRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    @SuppressWarnings("unused")
    private ElasticsearchOperations elasticsearchOperations;

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
                        .opprettetTidspunkt(now(CET))
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