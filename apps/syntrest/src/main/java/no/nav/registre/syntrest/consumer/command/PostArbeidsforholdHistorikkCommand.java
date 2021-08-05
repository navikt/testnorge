package no.nav.registre.syntrest.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdHistorikkCommand implements Callable<List<List<Arbeidsforhold>>> {

    private final WebClient webClient;
    private final List<Arbeidsforhold> arbeidsforhold;
    private final String syntAmeldingUrlPath;
    private final String queryString;

    private static final ParameterizedTypeReference<List<List<Arbeidsforhold>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Arbeidsforhold>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    public PostArbeidsforholdHistorikkCommand(List<Arbeidsforhold> arbeidsforhold, String syntAmeldingUrlPath, String queryString, WebClient webClient) {
        this.webClient = webClient;
        this.arbeidsforhold = arbeidsforhold;
        this.syntAmeldingUrlPath = syntAmeldingUrlPath;
        this.queryString = queryString;
    }

    @Override
    public List<List<Arbeidsforhold>> call() {
        try {
            var body = BodyInserters.fromPublisher(Mono.just(arbeidsforhold), REQUEST_TYPE);

            return webClient.post()
                    .uri(builder -> builder
                            .path(syntAmeldingUrlPath)
                            .query(queryString)
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(body)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception.", e);
            throw e;
        }
    }
}
