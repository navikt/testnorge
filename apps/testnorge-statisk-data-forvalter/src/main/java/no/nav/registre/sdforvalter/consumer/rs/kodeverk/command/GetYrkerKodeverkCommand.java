package no.nav.registre.sdforvalter.consumer.rs.kodeverk.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.kodeverk.response.KodeverkResponse;
import no.nav.registre.sdforvalter.util.CallIdUtil;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.*;

@RequiredArgsConstructor
@Slf4j
public class GetYrkerKodeverkCommand implements Callable<KodeverkResponse> {
    private final WebClient webClient;

    @Override
    public KodeverkResponse call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/kodeverk/Yrker/koder")
                                .build()
                )
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(KodeverkResponse.class)
                .doOnError(WebClientError.logTo(log))
                .block();
    }
}
