package no.nav.dolly.bestilling.etterlatte.command;

import co.elastic.clients.util.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakRequestDTO;
import no.nav.dolly.bestilling.etterlatte.dto.VedtakResponseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.apache.http.HttpHeaders.ACCEPT_ENCODING;

@Slf4j
@RequiredArgsConstructor
public class EtterlattePostCommand implements Callable<Mono<VedtakResponseDTO>> {

    private static final String VEDTAK_URL = "/dolly/api/v1/opprett-ytelse";

    private final WebClient webClient;
    private final VedtakRequestDTO vedtakRequest;
    private final String token;

    @Override
    public Mono<VedtakResponseDTO> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(VEDTAK_URL).build())
                .header(ACCEPT_ENCODING, ContentType.APPLICATION_JSON)
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .bodyValue(vedtakRequest)
                .retrieve()
                .bodyToMono(VedtakResponseDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> VedtakResponseDTO.of(WebClientError.describe(throwable)));
    }
}
