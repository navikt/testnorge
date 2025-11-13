package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pensjon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pensjon.PensjonTestdataPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pensjon.PensjonTestdataResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@RequiredArgsConstructor
@Slf4j
public class PostPensjonTestdataPersonCommand implements Callable<Mono<PensjonTestdataResponse>> {

    private final WebClient webClient;
    private final PensjonTestdataPerson person;
    private final String token;

    @Override
    public Mono<PensjonTestdataResponse> call() {
        log.info("Oppretter ny pensjon testdata person: \n{}", person);
        return webClient
                .post()
                .uri(builder -> builder.path("/pensjon/api/v1/person").build())
                .header(CALL_ID, NAV_CALL_ID)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromPublisher(Mono.just(person), PensjonTestdataPerson.class))
                .retrieve()
                .bodyToMono(PensjonTestdataResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
