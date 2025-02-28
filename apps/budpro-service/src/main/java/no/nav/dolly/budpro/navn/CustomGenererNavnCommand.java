package no.nav.dolly.budpro.navn;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.libs.command.Command;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@AllArgsConstructor
public class CustomGenererNavnCommand implements Callable<NavnDTO[]> {

    private final WebClient webClient;
    private final Mono<AccessToken> accessToken;
    private Long seed;
    private final Integer antall;

    @Override
    public NavnDTO[] call() {
        return Command.fetch(
                webClient,
                accessToken,
                builder -> builder
                        .path("/api/v1/navn")
                        .queryParamIfPresent("seed", Optional.ofNullable(seed))
                        .queryParam("antall", antall)
                        .build(),
                NavnDTO[].class);
    }

}
