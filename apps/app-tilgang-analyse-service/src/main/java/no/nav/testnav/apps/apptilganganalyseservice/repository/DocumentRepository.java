package no.nav.testnav.apps.apptilganganalyseservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.DocumentEntity;


public interface DocumentRepository extends ReactiveCrudRepository<DocumentEntity, String> {
    Mono<DocumentEntity> findByShaAndRepo(String sha, String repo);
}
