package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.domain.projection.DollyTeam2Fragment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeamRepository extends ReactiveCrudRepository<Team, Long> {

    Mono<Team> findByNavn(String navn);

    @Query("""
            select t.bruker_id brukerId, count(*) antall
            from Team t
            join team_bruker tb on t.id = tb.team_id
            group by brukerId
            """)
    Flux<DollyTeam2Fragment> findAllTeamBrukere();
}