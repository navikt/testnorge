package no.nav.testnav.apps.apptilganganalyseservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

import no.nav.testnav.apps.apptilganganalyseservice.consumer.command.GetBlobFromShaCommand;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.command.SearchCodeCommand;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchArgs;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchResults;


@Component
public class GithubConsumer {
    private final WebClient webClient;
    private static final int PAGE_SIZE = 100;

    public GithubConsumer(@Value("${consumers.github.url}") String url) {
        this.webClient =  WebClient
                .builder()
                .baseUrl(url)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }

    public Mono<SearchResults> search(SearchArgs args, String repo) {
        return search(new SearchResults(repo), args, 1)
                .cache(
                        value -> Duration.ofMinutes(1),
                        throwable -> Duration.ZERO,
                        () -> Duration.ZERO
                );
    }

    private Mono<SearchResults> search(SearchResults results, SearchArgs args, Integer page) {
        var command = new SearchCodeCommand(webClient, args.toString(), page, PAGE_SIZE);
        return command.call().flatMap(dto -> {
            if (page * PAGE_SIZE < dto.getTotalCount()) {
                return search(results.concat(dto), args, page + 1);
            }
            return Mono.just(results.concat(dto));
        });
    }

    public Mono<byte[]> getBlobFromSha(String sha, String repo) {
        return new GetBlobFromShaCommand(webClient, sha, repo).call();
    }
}
