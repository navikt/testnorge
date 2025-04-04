package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class VegadresseServiceCommand implements Callable<Flux<VegadresseDTO>> {

    private static final String ADRESSER_VEG_URL = "/api/v1/adresser/veg";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<VegadresseDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(ADRESSER_VEG_URL).build())
                .header("antall", "1")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(VegadresseDTO.class)
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound ||
                                throwable instanceof WebClientResponseException.BadRequest ||
                                Exceptions.isRetryExhausted(throwable),
                        throwable -> Mono.just(defaultAdresse()));
    }

    public static VegadresseDTO defaultAdresse() {

        return VegadresseDTO.builder()
                .matrikkelId("285693617")
                .adressenavn("FYRSTIKKALLÉEN")
                .postnummer("0661")
                .husnummer(2)
                .kommunenummer("0301")
                .poststed("Oslo")
                .build();
    }
}
