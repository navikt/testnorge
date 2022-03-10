package no.nav.testnav.apps.personservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.HendelseDTO;
import no.nav.testnav.apps.personservice.consumer.dto.pdl.NavnDTO;
import no.nav.testnav.apps.personservice.consumer.header.PdlHeaders;
import no.nav.testnav.apps.personservice.domain.Person;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostNavnCommand implements Callable<Mono<HendelseDTO>> {
    private final WebClient webClient;
    private final Person person;
    private final String kilde;
    private final String token;

    @Override
    public Mono<HendelseDTO> call() {
        NavnDTO body = new NavnDTO(person, kilde);
        return webClient.post()
                .uri("/pdl-testdata/api/v1/bestilling/navn")
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, person.getIdent())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(body), NavnDTO.class))
                .retrieve()
                .bodyToMono(HendelseDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
