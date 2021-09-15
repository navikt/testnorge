package no.nav.testnav.apps.apptilganganalyseservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.ApplicationEntity;


public interface ApplicationRepository extends ReactiveCrudRepository<ApplicationEntity, String> {
    Mono<ApplicationEntity> findByShaAndRepo(String sha, String repo);
}
