package no.nav.dolly.bestilling.dokarkiv.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivRequest;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class DokarkivPostCommand implements Callable<Mono<DokarkivResponse>> {

    private final WebClient webClient;
    private final String environment;
    private final DokarkivRequest dokarkivRequest;
    private final String token;

    @Override
    public Mono<DokarkivResponse> call() {
        return webClient
                .post()
                .uri(builder ->
                        builder.path("/api/{miljo}/v1/journalpost")
                                .queryParam("forsoekFerdigstill", isTrue(dokarkivRequest.getFerdigstill()))
                                .build(environment))
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(dokarkivRequest)
                .retrieve()
                .bodyToMono(DokarkivResponse.class)
                .map(response -> {
                    response.setMiljoe(environment);
                    return response;
                })
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> DokarkivResponse.of(WebClientError.describe(throwable), environment));
    }

}
