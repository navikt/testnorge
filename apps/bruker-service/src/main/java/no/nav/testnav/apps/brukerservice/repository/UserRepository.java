package no.nav.testnav.apps.brukerservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<UserEntity, String> {
    Mono<UserEntity> findByBrukernavn(String brukernavn);

    Mono<Boolean> existsByBrukernavn(String brukernavn);
}
