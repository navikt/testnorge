package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkDTO;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.AdressehistorikkRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetAdressehistorikkCommand implements Callable<Flux<AdressehistorikkDTO>> {

    private final WebClient webClient;
    private final AdressehistorikkRequest request;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<AdressehistorikkDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v1/personer/adressehistorikk")
                        .queryParam("miljoer", miljoer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToFlux(AdressehistorikkDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> WebClientError.log(throwable, log));
    }

}
