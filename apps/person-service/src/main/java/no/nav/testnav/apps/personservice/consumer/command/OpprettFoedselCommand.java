package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.FoedselDTO;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.HendelseDTO;
import no.nav.testnav.apps.personservice.consumer.header.PdlHeaders;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OpprettFoedselCommand implements Callable<Mono<HendelseDTO>> {
    private final WebClient webClient;
    private final FoedselDTO dto;
    private final String token;
    private final String ident;

    @Override
    public Mono<HendelseDTO> call() {
        return webClient.post()
                .uri("/pdl-testdata/api/v1/bestilling/foedsel")
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(dto), FoedselDTO.class))
                .retrieve()
                .bodyToMono(HendelseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
