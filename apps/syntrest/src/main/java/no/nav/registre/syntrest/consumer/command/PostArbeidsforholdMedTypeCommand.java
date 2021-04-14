package no.nav.registre.syntrest.consumer.command;

import java.util.concurrent.Callable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.domain.aareg.amelding.ArbeidsforholdAmelding;
import no.nav.registre.syntrest.domain.aareg.amelding.ArbeidsforholdPeriode;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdMedTypeCommand implements Callable<ArbeidsforholdAmelding> {

    private final WebClient webClient;
    private final ArbeidsforholdPeriode request;
    private final String syntAmeldingUrlPath;

    public PostArbeidsforholdMedTypeCommand(ArbeidsforholdPeriode request, String syntAmeldingUrlPath, WebClient webClient) {
        this.webClient = webClient;
        this.request = request;
        this.syntAmeldingUrlPath = syntAmeldingUrlPath;
    }

    @Override
    public ArbeidsforholdAmelding call() {
        try {
            var body = BodyInserters.fromPublisher(Mono.just(request), ArbeidsforholdPeriode.class);

            return webClient.post()
                    .uri(builder -> builder.path(syntAmeldingUrlPath).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .retrieve()
                    .bodyToMono(ArbeidsforholdAmelding.class)
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception.", e);
            throw e;
        }
    }

}
