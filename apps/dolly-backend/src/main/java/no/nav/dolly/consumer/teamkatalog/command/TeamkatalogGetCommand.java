package no.nav.dolly.consumer.teamkatalog.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TeamkatalogGetCommand implements Callable<Mono<TeamkatalogDTO>> {

    private static final String TEAM_URL = "/member/membership/byUserEmail";

    private final WebClient webClient;
    private final String epost;
//    private final String token;

    @Override
    public Mono<TeamkatalogDTO> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TEAM_URL)
                        .queryParam("email", epost)
                        .build())
//                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(TeamkatalogDTO.class)
                .map(response -> {
                    response.setEpost(epost);
                    response.setTeamNavn(response.getTeams().stream()
                            .map(TeamkatalogDTO.Team::getName)
                            .toList());
                    return response;
                })
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    return Mono.just(TeamkatalogDTO.builder()
                            .status(description.getStatus())
                            .feilmelding(description.getMessage())
                            .build());
                });
    }
}
