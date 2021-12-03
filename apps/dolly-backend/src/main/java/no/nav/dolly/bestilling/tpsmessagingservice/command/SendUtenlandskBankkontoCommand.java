package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.personservice.domain.AktoerIdent;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class SendUtenlandskBankkontoCommand implements Callable<Mono<ResponseEntity<AktoerIdent>>> {

    private static final String AKTOERID_URL = "/api/v1/personer";
    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String callId;

    @Override
    public Mono<ResponseEntity<AktoerIdent>> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(AKTOERID_URL)
                        .pathSegment(ident)
                        .pathSegment("aktoerId")
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(AktoerIdent.class);
    }
}
