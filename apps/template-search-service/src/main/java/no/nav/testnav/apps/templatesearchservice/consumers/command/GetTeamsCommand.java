package no.nav.testnav.apps.templatesearchservice.consumers.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.templatesearchservice.consumers.dto.DollyTeamDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTeamsCommand implements Callable<Mono<List<DollyTeamDTO>>> {

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<DollyTeamDTO>> call() {
        return webClient.get()
                .uri("/api/v1/team")
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(DollyTeamDTO.class)
                .collectList()
                .timeout(Duration.ofSeconds(10));
    }
}
