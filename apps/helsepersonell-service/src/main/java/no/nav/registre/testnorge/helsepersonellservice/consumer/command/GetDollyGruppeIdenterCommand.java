package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.helsepersonellservice.domain.RsTestgruppeMedBestillingId;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.AUTHORIZATION;

@RequiredArgsConstructor
public class GetDollyGruppeIdenterCommand implements Callable<Mono<RsTestgruppeMedBestillingId>> {
    private final int gruppeId;
    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<RsTestgruppeMedBestillingId> call() {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/gruppe/{gruppeId}")
                                    .build(gruppeId)
                    )
                    .header(AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(RsTestgruppeMedBestillingId.class);
    }
}
