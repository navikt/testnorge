package no.nav.dolly.bestilling.sigrunstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubRequest;
import no.nav.dolly.bestilling.sigrunstub.dto.SigrunstubResponse;
import no.nav.dolly.util.RequestHeaderUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
@Slf4j
public class SigurunstubPutCommand implements Callable<Mono<SigrunstubResponse>> {

    private static final String CONSUMER = "Dolly";

    private final WebClient webClient;
    private final String url;
    private final List<? extends SigrunstubRequest> request;
    private final String token;

    @Override
    public Mono<SigrunstubResponse> call() {
        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .header(ACCEPT, APPLICATION_JSON_VALUE)
                .header(HEADER_NAV_CALL_ID, RequestHeaderUtil.getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SigrunstubResponse.class)
                .map(response -> {
                    for (int i = 0; i < response.getOpprettelseTilbakemeldingsListe().size(); i++) {
                        response.getOpprettelseTilbakemeldingsListe().get(i).setInntektsaar(
                                request.get(i).getInntektsaar());
                    }
                    return response;
                })
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(SigrunstubResponse.builder()
                        .status(WebClientFilter.getStatus(error))
                        .melding(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
