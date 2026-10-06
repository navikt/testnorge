package no.nav.testnav.apps.statusfrontend.fagsystem.krr.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class DeleteKrrContactInformationCommand implements Callable<Mono<Void>> {

    private final WebClient webClient;
    private final String token;
    private final String contactId;
    private final RunId runId;
    private final Duration timeout;

    @Override
    public Mono<Void> call() {
        return webClient.delete()
                .uri("/krrstub/api/v2/kontaktinformasjon/{id}", contactId)
                .headers(headers -> headers.setBearerAuth(token))
                .header("Nav-Consumer-Id", "Dolly")
                .exchangeToMono(response -> {
                    log.info("KRR-sletting svarte: runId={}, httpStatus={}",
                            runId.value(), response.statusCode().value());
                    if (response.statusCode().is2xxSuccessful()
                            || response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody();
                    }
                    return response.createException()
                            .flatMap(Mono::error);
                })
                .timeout(timeout);
    }
}
