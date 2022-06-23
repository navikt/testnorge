package no.nav.testnav.apps.apptilganganalyseservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.command.GetBlobFromShaCommand;
import no.nav.testnav.apps.apptilganganalyseservice.consumer.command.SearchCodeCommand;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchArgs;
import no.nav.testnav.apps.apptilganganalyseservice.domain.SearchResults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GithubConsumer {
    private static final int PAGE_SIZE = 100;
    private final WebClient webClient;

    public GithubConsumer(@Value("${consumers.github.url}") String url,
                          ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Mono<SearchResults> search(SearchArgs args, String owner, String repo) {
        log.info("Søker etter '{}'.", args.toString());
        return search(new SearchResults(repo, owner), args, 1)
                .doOnNext(value -> log.info("Fant {} (søk='{}').", value.getItems().size(), args));
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

    public Mono<byte[]> getBlobFromSha(String sha, String owner, String repo) {
        return new GetBlobFromShaCommand(webClient, sha, owner, repo).call();
    }
}
