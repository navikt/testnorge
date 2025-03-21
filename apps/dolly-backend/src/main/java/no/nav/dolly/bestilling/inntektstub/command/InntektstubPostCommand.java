package no.nav.dolly.bestilling.inntektstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.util.TokenXUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class InntektstubPostCommand implements Callable<Flux<Inntektsinformasjon>> {

    private static final String INNTEKTER_URL = "/api/v2/inntektsinformasjon";

    private final WebClient webClient;
    private final List<Inntektsinformasjon> inntektsinformasjon;
    private final String token;

    @Override
    public Flux<Inntektsinformasjon> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(INNTEKTER_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(UserConstant.USER_HEADER_JWT, TokenXUtil.getUserJwt())
                .bodyValue(inntektsinformasjon)
                .retrieve()
                .bodyToFlux(Inntektsinformasjon.class)
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Lagring av Instdata feilet: {}", description.getMessage(), throwable);
                    return Inntektsinformasjon.of(description);
                })
                .retryWhen(WebClientError.is5xxException());
    }

}
