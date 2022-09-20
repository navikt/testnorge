package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.helsepersonellservice.domain.RsTestgruppeMedBestillingId;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.Callable;

import static java.lang.String.format;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.NAV_CONSUMER_ID;
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
                    .header(CONSUMER_ID, NAV_CONSUMER_ID)
                    .header(CALL_ID, format("%s %s", NAV_CONSUMER_ID, UUID.randomUUID()))
                    .retrieve()
                    .bodyToMono(RsTestgruppeMedBestillingId.class);
    }
}
