package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.personSearch.PersonSearchRequest;
import no.nav.testnav.libs.dto.personsearchservice.v1.PersonDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;

@Slf4j
@AllArgsConstructor
public class PersonSearchCommand implements Callable<Mono<List<PersonDTO>>> {

    private final PersonSearchRequest request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<PersonDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<PersonDTO>> call(){
        try {
            return webClient.post()
                    .uri(builder ->
                            builder.path("/api/v1/person")
                                    .build()
                    )
                    .header(AUTHORIZATION, "Bearer " + token)
                    .body(BodyInserters.fromPublisher(Mono.just(request), PersonSearchRequest.class))
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE);
        } catch (Exception e) {
            log.error("Kunne ikke hente søkeresultat.", e);
            return Mono.empty();
        }
    }
}
