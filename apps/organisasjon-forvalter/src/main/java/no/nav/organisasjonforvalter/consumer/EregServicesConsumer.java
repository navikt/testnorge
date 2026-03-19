package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.organisasjonforvalter.config.Consumers;
import no.nav.organisasjonforvalter.consumer.command.EregServicesCommand;
import no.nav.organisasjonforvalter.dto.responses.ereg.EregServicesResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

import static java.util.Objects.nonNull;

@Slf4j
@Service
public class EregServicesConsumer {

    private final WebClient webClientQ1;
    private final WebClient webClientQ2;
    private final WebClient webClientQ4;

    public EregServicesConsumer(
            Consumers consumers,
            WebClient webClient) {
        val serverProperties = consumers.getEregServices();
        this.webClientQ1 = webClient
                .mutate()
                .baseUrl(serverProperties.getMiljoe("q1").getUrl())
                .build();
        this.webClientQ2 = webClient
                .mutate()
                .baseUrl(serverProperties.getMiljoe("q2").getUrl())
                .build();
        this.webClientQ4 = webClient
                .mutate()
                .baseUrl(serverProperties.getMiljoe("q4").getUrl())
                .build();
    }

    public Flux<EregServicesResponse> getStatus(String orgnummer, Set<String> miljoer) {

        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> switch (miljoe) {
                    case "q1" -> Mono.just(webClientQ1).zipWith(Mono.just(miljoe));
                    case "q2" -> Mono.just(webClientQ2).zipWith(Mono.just(miljoe));
                    case "q4" -> Mono.just(webClientQ4).zipWith(Mono.just(miljoe));
                    default -> Mono.error(new IllegalArgumentException("Ukjent miljoe: " + miljoe));
                })
                .flatMap(tuple ->
                        new EregServicesCommand(tuple.getT1(), orgnummer, tuple.getT2()).call())
                .filter(response -> nonNull(response.getOrganisasjon()));
    }
}
