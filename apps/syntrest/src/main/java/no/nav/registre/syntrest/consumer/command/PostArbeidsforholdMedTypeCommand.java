package no.nav.registre.syntrest.consumer.command;

import java.util.concurrent.Callable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdMedTypeCommand implements Callable<Arbeidsforhold> {

    private final WebClient webClient;
    private final ArbeidsforholdPeriode request;
    private final String syntAmeldingUrlPath;
    private final String queryString;

    public PostArbeidsforholdMedTypeCommand(ArbeidsforholdPeriode request, String syntAmeldingUrlPath, String queryString, WebClient webClient) {
        this.webClient = webClient;
        this.request = request;
        this.syntAmeldingUrlPath = syntAmeldingUrlPath;
        this.queryString = queryString;
    }

    @Override
    public Arbeidsforhold call() {
        try {
            var body = BodyInserters.fromPublisher(Mono.just(request), ArbeidsforholdPeriode.class);

            return webClient.post()
                    .uri(builder -> builder
                            .path(syntAmeldingUrlPath)
                            .query(queryString)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .retrieve()
                    .bodyToMono(Arbeidsforhold.class)
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception.", e);
            throw e;
        }
    }

}
