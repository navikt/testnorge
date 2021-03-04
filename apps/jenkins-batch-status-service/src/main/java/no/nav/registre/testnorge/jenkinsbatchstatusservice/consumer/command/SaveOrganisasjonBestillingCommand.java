package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import io.netty.channel.unix.Errors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SaveOrganisasjonBestillingCommand implements Callable<Long> {
    private final WebClient webClient;
    private final String token;
    private final String uuid;

    @Override
    public Long call() {
        return webClient
                .put()
                .uri(builder -> builder.path("/api/v1/order/{uuid}").build(uuid))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Long.class)
                .retryWhen(Retry.max(3).filter(ex -> {
                    log.error("Feil ved lagring ved bestilling.");
                    return ex instanceof Errors.NativeIoException;
                }))
                .block();
    }
}
