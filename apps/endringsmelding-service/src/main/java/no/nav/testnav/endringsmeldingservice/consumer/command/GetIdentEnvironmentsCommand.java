package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.TpsIdentStatusDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetIdentEnvironmentsCommand implements Callable<Flux<TpsIdentStatusDTO>> {
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<TpsIdentStatusDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/identer")
                        .queryParam("identer", ident)
                        .queryParam("includeProd", false)
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(TpsIdentStatusDTO.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
