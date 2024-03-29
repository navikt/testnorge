package no.nav.testnav.apps.apptilganganalyseservice.service;

import lombok.AllArgsConstructor;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.GithubConsumer;
import no.nav.testnav.apps.apptilganganalyseservice.domain.DocumentType;
import no.nav.testnav.apps.apptilganganalyseservice.domain.ItemResult;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchResults;
import no.nav.testnav.apps.apptilganganalyseservice.repository.DocumentRepository;
import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.DocumentEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
abstract class ReadConfigService {
    final GithubConsumer githubConsumer;
    final DocumentRepository documentRepository;

    private Mono<DocumentEntity> fetchAndSave(ItemResult item, String owner, String repo) {
        return documentRepository
                .findByShaAndRepo(item.getSha(), repo)
                .switchIfEmpty(Mono.defer(() -> githubConsumer
                        .getBlobFromSha(item.getSha(), owner, repo)
                        .map(String::new)
                        .map(content -> DocumentEntity
                                .builder()
                                .sha(item.getSha())
                                .content(content)
                                .type(getType())
                                .repo(repo)
                                .owner(owner)
                                .path(item.getPath())
                                .build()
                        )
                        .flatMap(documentRepository::save)
                ));
    }

    Flux<DocumentEntity> resolve(SearchResults results) {
        return Flux.concat(
                results.getItems()
                        .stream()
                        .map(item -> fetchAndSave(item, results.getOwner(), results.getRepo()))
                        .toList()
        );
    }

    abstract DocumentType getType();

}