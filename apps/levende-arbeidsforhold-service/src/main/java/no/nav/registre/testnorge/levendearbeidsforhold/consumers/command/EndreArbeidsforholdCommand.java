package no.nav.registre.testnorge.levendearbeidsforhold.consumers.command;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdRequest;
import no.nav.testnav.libs.dto.syntrest.v1.ArbeidsforholdResponse;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class EndreArbeidsforholdCommand implements Callable<List<Arbeidsforhold>> {
    private final WebClient webClient;
    private final Arbeidsforhold requests;
    private final String token;
    private final ObjectMapper objectMapper;
    private final String navArbeidsforholdKilde = "Dolly-doedsfall-hendelse" ;
    @SneakyThrows
    @Override
    public List<Arbeidsforhold> call() throws Exception {

        var arbeidsforhold = webClient
                .put()
                .uri("/api/v1/arbeidsforhold/{navArbeidsforholdId}",
                        requests.getNavArbeidsforholdId())
                .body(requests, Arbeidsforhold.class)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Arbeidsforhold-Id", navArbeidsforholdKilde)
                .header("Nav-Arbeidsforhold-Periode", requests.getAnsettelsesperiode().toString())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Arbeidsforhold>() {
                }).retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(error -> {
                    try {
                        log.error("Feil ved oppdatering av arbeidsforhold med body: {}.", objectMapper.writeValueAsString(requests), error);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved convertering av body til string.", e);
                    }
                }).block();

        return arbeidsforhold == null ? List.of() : List.of(arbeidsforhold);
    }
}


