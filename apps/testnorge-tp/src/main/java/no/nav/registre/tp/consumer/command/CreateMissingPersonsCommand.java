package no.nav.registre.tp.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.tp.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

public class CreateMissingPersonsCommand extends TpPostStringListCommand {

    public CreateMissingPersonsCommand(List<String> fnrs, WebClient webClient) {
        super("/api/tp/missing-persons", fnrs, webClient);
    }

}
