package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.IdentMiljoeDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetEksistererPersonCommand implements Callable<Flux<IdentMiljoeDTO>> {

    private final WebClient webClient;
    private final Set<String> identer;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<IdentMiljoeDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/identer")
                        .queryParam("identer", identer)
                        .queryParamIfPresent("miljoer", Optional.ofNullable(miljoer))
                        .queryParam("includeProd", false)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(IdentMiljoeDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

}
