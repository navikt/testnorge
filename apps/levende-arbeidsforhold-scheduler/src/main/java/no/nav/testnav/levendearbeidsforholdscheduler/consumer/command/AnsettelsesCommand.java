package no.nav.testnav.levendearbeidsforholdscheduler.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class AnsettelsesCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<String> call()  {

        return webClient.post().uri(
                builder -> builder.path("/api/v1/ansettelse")
                        .build())
                        .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String.class);
    }
}
