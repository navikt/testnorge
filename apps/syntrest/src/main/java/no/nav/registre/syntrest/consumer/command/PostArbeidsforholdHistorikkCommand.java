package no.nav.registre.syntrest.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.domain.aareg.amelding.ArbeidsforholdAmelding;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdHistorikkCommand implements Callable<List<ArbeidsforholdAmelding>> {

    private final WebClient webClient;
    private final ArbeidsforholdAmelding arbeidsforhold;
    private final String syntAmeldingUrlPath;
    private final String queryString;

    public PostArbeidsforholdHistorikkCommand(ArbeidsforholdAmelding arbeidsforhold, String syntAmeldingUrlPath, String queryString, WebClient webClient) {
        this.webClient = webClient;
        this.arbeidsforhold = arbeidsforhold;
        this.syntAmeldingUrlPath = syntAmeldingUrlPath;
        this.queryString = queryString;
    }

    @Override
    public List<ArbeidsforholdAmelding> call() {
        List<ArbeidsforholdAmelding> response;
        try {
            var body = BodyInserters.fromPublisher(Mono.just(arbeidsforhold), ArbeidsforholdAmelding.class);

            response = webClient.post()
                    .uri(builder -> builder
                            .path(syntAmeldingUrlPath)
                            .query(queryString)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .retrieve()
                    .bodyToFlux(ArbeidsforholdAmelding.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception.", e);
            throw e;
        }
        return response;
    }
}
