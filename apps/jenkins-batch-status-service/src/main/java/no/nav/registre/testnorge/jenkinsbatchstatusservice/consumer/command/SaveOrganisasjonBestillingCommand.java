package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import io.netty.channel.unix.Errors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

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
                .retryWhen(Retry.max(3).filter(ex -> ex instanceof Errors.NativeIoException))
                .block();
    }
}
