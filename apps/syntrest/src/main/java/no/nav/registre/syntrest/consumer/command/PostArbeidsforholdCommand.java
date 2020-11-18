package no.nav.registre.syntrest.consumer.command;

import java.util.concurrent.Callable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.domain.amelding.Arbeidsforhold;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdCommand implements Callable<Arbeidsforhold> {

    private final WebClient webClient;
    private final Arbeidsforhold arbeidsforhold;
    private final String syntAmeldingUrlPath;

    public PostArbeidsforholdCommand(Arbeidsforhold arbeidsforhold, String syntAmeldingUrlPath, WebClient webClient) {
        this.webClient = webClient;
        this.arbeidsforhold = arbeidsforhold;
        this.syntAmeldingUrlPath = syntAmeldingUrlPath;
    }

    @Override
    public Arbeidsforhold call() {
        Arbeidsforhold response;
        try {
            var body = BodyInserters.fromPublisher(Mono.just(arbeidsforhold), Arbeidsforhold.class);

            response = webClient.post()
                    .uri(builder -> builder.path(syntAmeldingUrlPath).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .retrieve()
                    .bodyToMono(Arbeidsforhold.class)
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception.", e);
            throw e;
        }
        return response;
    }
}
