package no.nav.registre.testnorge.generersyntameldingservice.consumer.command;

import java.util.concurrent.Callable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.domain.dto.aareg.amelding.Arbeidsforhold;
import no.nav.testnav.libs.domain.dto.aareg.amelding.ArbeidsforholdPeriode;
import reactor.core.publisher.Mono;

@Slf4j
public class PostArbeidsforholdCommand implements Callable<Arbeidsforhold> {

    private final WebClient webClient;
    private final ArbeidsforholdPeriode periode;
    private final String path;
    private final String token;

    public PostArbeidsforholdCommand(ArbeidsforholdPeriode periode, WebClient webClient, String arbeidsforholdType, String token) {
        this.webClient = webClient;
        this.periode = periode;
        this.token = token;
        this.path = String.format("/api/v1/arbeidsforhold/start/%s", arbeidsforholdType);
    }

    @Override
    public Arbeidsforhold call() {
        try {
            var body = BodyInserters.fromPublisher(Mono.just(periode), ArbeidsforholdPeriode.class);

            return webClient.post()
                    .uri(builder -> builder
                            .path(path)
                            .queryParam("avvik", "false")
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .bodyToMono(Arbeidsforhold.class)
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception: " + e.getMessage());
            throw e;
        }
    }

}
