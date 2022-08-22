package no.nav.dolly.bestilling.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.CommonKeysAndUtils;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.TokenXUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class InstdataDeleteCommand implements Callable<Flux<InstdataResponse>> {

    private static final String INSTDATA_URL = "/api/v1/ident";
    private static final String DELETE_POST_FMT_BLD = INSTDATA_URL + "/batch";

    private static final String INST_IDENTER_QUERY = "identer";
    private static final String INST_MILJOE_QUERY = "miljoe";

    private final WebClient webClient;
    private final List<String> identer;
    private final String environment;
    private final String token;

    @Override
    public Flux<InstdataResponse> call() {

        return webClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path(DELETE_POST_FMT_BLD)
                        .queryParam(INST_IDENTER_QUERY, identer)
                        .queryParam(INST_MILJOE_QUERY, environment)
                        .build())
                .header(CommonKeysAndUtils.HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID, CommonKeysAndUtils.CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, TokenXUtil.getUserJwt())
                .retrieve()
                .bodyToFlux(InstdataResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
