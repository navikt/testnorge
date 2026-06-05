package no.nav.dolly.consumer.teamkatalog.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.teamkatalog.dto.TeamkatalogDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TeamkatalogGetCommand implements Callable<Flux<TeamkatalogDTO>> {

    private static final String TEAM_URL = "/member/simpleMemberships/byUserEmail";

    private final WebClient webClient;
    private final List<String> epost;
    private final String token;

    @Override
    public Flux<TeamkatalogDTO> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(TEAM_URL)
                        .queryParam("onlyActive", true)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(Mono.just(epost), List.class)
                .retrieve()
                .bodyToFlux(TeamkatalogDTO.class)
                .map(response -> {
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
