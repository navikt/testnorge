package no.nav.registre.sdforvalter.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sdforvalter.consumer.rs.response.KodeverkResponse;
import no.nav.registre.sdforvalter.util.CallIdUtil;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.registre.sdforvalter.domain.CommonKeysAndUtils.CONSUMER;

@RequiredArgsConstructor
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
                .block();
    }
}
