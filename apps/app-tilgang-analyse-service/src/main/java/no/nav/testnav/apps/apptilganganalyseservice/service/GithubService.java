package no.nav.testnav.apps.apptilganganalyseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.GithubConsumer;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchArgs;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchResults;

@Service
@RequiredArgsConstructor
public class GithubService {
    private final GithubConsumer githubConsumer;

    public Mono<SearchResults> search(String appName, String repo) {
        var searchArgs = SearchArgs.Builder
                .builder()
                .addLanguage("yml")
                .addLanguage("yaml")
                .addSearchString("kind: \"Application\"")
                .addSearchString("application: " + appName)
                .repo(repo)
                .build();
        return githubConsumer
                .search(searchArgs, repo);
    }

    public Mono<byte[]> getBlob(String sha, String repo) {
        return githubConsumer.getBlobFromSha(sha, repo);
    }

}
