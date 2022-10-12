package no.nav.dolly.bestilling.sykemelding.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class PostSyntSykemeldingCommand {

    private static final String SYNT_SYKEMELDING_URL = "/api/v1/synt-sykemelding";
    private final WebClient webClient;
    private final String token;
    private final SyntSykemeldingRequest sykemeldingRequest;

    public Mono<ResponseEntity<String>> call() {
        return webClient.post().uri(uriBuilder -> uriBuilder
                        .path(SYNT_SYKEMELDING_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(sykemeldingRequest)
                .retrieve()
                .toEntity(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
