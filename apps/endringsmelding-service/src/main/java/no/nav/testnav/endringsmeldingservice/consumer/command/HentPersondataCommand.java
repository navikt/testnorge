package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class HentPersondataCommand implements Callable<Flux<PersonMiljoeDTO>> {

    private final WebClient webClient;
    private final String ident;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<PersonMiljoeDTO> call() {
        return webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v2/personer/ident")
                        .queryParam("miljoer", miljoer)
                        .build())
                .bodyValue(ident)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(PersonMiljoeDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

}
