package no.nav.testnav.apps.brukerservice.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {

    Mono<UserEntity> findByBrukernavn(String brukernavn);

    Mono<Boolean> existsByBrukernavn(String brukernavn);

    @Query("select * from user_entity as ue " +
            "join user_entity as u on u.organisasjonsnummer = ue.organisasjonsnummer " +
            "and u.id = :brukerId")
    Flux<UserEntity> findBrukereISammeOrganisasjoner(@Param("brukerId") String brukerId);
}
