package no.nav.testnav.apps.statusfrontend.fagsystem.arena.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetArenaUserCommand implements Callable<Mono<ArenaResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String environment;
    private final ArenaRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<ArenaResourceStatus> call() {
        return webClient.get()
                .uri("/arena/{environment}/arena/syntetiser/brukeroppfolging/personstatusytelse",
                        environment)
                .headers(headers -> headers.setBearerAuth(token))
                .header("fodselsnr", ident)
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NO_CONTENT
                            || response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody().thenReturn(ArenaResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus)
                                .defaultIfEmpty(ArenaResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<ArenaResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private ArenaResourceStatus toStatus(JsonNode response) {
        if (!response.isObject()) {
            throw new IllegalStateException("Arena-oppslaget returnerte ugyldig respons.");
        }
        var user = expectedRequest.nyeBrukere().getFirst();
        var registered = !response.path("registrertDato").isMissingNode()
                && !response.path("registrertDato").isNull();
        var inactive = !response.path("sistInaktivDato").isMissingNode()
                && !response.path("sistInaktivDato").isNull();
        var serviceGroup = response.path("servicegruppe").path("kode").asString();
        return new ArenaResourceStatus(
                false,
                registered && !inactive,
                registered && !inactive && user.kvalifiseringsgruppe().equals(serviceGroup),
                registered && inactive);
    }
}
