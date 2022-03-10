package no.nav.registre.inst.consumer.rs.command;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriTemplate;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;
import static no.nav.registre.inst.properties.HttpRequestConstants.ACCEPT;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

@Slf4j
@RequiredArgsConstructor
public class ExistsInstitusjonsoppholdCommand implements Callable<HttpStatus> {

    private final WebClient webClient;
    private final UriTemplate inst2WebApiServerUrl;
    private final String miljoe;
    private final LocalDate date;
    private final String tssEksternId;
    private final String callId;
    private final String consumerId;

    @SneakyThrows
    @Override
    public HttpStatus call() {
        try {
            var response = webClient.get().uri(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/institusjon/oppslag/tssEksternId/{tssEksternId}?date={date}")
                            .expand(tssEksternId, date))
                    .header(ACCEPT, "application/json")
                    .header(HEADER_NAV_CALL_ID, callId)
                    .header(HEADER_NAV_CONSUMER_ID, consumerId)
                    .retrieve()
                    .toBodilessEntity()
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

            return nonNull(response) ? response.getStatusCode() : HttpStatus.NOT_FOUND;
        } catch (WebClientResponseException e) {
            log.debug("Institusjon med tssEksternId {} er ikke gyldig på dato {}.", tssEksternId, date);
            return e.getStatusCode();
        }
    }
}
