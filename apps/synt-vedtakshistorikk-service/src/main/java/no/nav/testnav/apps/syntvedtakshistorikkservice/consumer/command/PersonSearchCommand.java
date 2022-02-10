package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.EndreInnsatsbehovRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.EndreInnsatsbehovResponse;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.personSearch.PersonDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class PersonSearchCommand implements Callable<Mono<PersonDTO>> {

    private final PersonSearchRequest request;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<PersonDTO> call(){
        try {
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/person")
                                    .build()
                    )
                    .header(AUTHORIZATION, "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(request), PersonSearchRequest.class))
                    .retrieve()
                    .bodyToMono(PersonDTO.class);
        } catch (Exception e) {
            log.error("Kunne ikke hente søkeresultat.", e);
            return Mono.empty();
        }
    }
}
