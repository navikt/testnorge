package no.nav.testnav.apps.apptilganganalyseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

import no.nav.testnav.apps.apptilganganalyseservice.domain.Application;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchResults;
import no.nav.testnav.apps.apptilganganalyseservice.repository.ApplicationRepository;
import no.nav.testnav.apps.apptilganganalyseservice.repository.entity.ApplicationEntity;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final GithubService githubService;
    private final ApplicationRepository applicationRepository;

    private Mono<ApplicationEntity> fetchAndSave(String sha, String repo) {
        return applicationRepository
                .findByShaAndRepo(sha, repo)
                .switchIfEmpty(Mono.defer(() -> githubService
                        .getBlob(sha, repo)
                        .map(String::new)
                        .map(content -> ApplicationEntity.builder().sha(sha).content(content).repo(repo).build())
                        .flatMap(applicationRepository::save)
                ));
    }


    private Flux<Application> resolve(SearchResults results) {
        return Flux.concat(
                results.getSha()
                        .stream()
                        .map(sha -> fetchAndSave(sha, results.getRepo()))
                        .collect(Collectors.toList())
        ).map(value -> new Application(value.getContent()));
    }

    public Flux<Application> fetchAppsBy(String appName, String repo) {
        return githubService.search(appName, repo)
                .flatMapMany(this::resolve);
    }

}
