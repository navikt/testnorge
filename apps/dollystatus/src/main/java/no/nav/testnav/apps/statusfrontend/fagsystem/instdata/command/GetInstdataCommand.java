package no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataRecord;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataSearchRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetInstdataCommand implements Callable<Mono<InstdataResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final InstdataSearchRequest request;
    private final InstdataRecord expectedRecord;
    private final Duration timeout;

    @Override
    public Mono<InstdataResourceStatus> call() {
        return webClient.post()
                .uri("/inst/api/v1/institusjonsopphold/person/soek")
                .headers(headers -> headers.setBearerAuth(token))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::toStatus)
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private InstdataResourceStatus toStatus(JsonNode response) {
        if (!response.isObject()) {
            throw new IllegalStateException("Instdata-oppslaget returnerte ugyldig respons.");
        }
        var entries = response.path(request.environments().getFirst());
        if (entries.isMissingNode() || entries.isNull()) {
            return new InstdataResourceStatus(true, false);
        }
        if (!entries.isArray()) {
            throw new IllegalStateException("Instdata-oppslaget mangler forventet miljø.");
        }
        var expectedDataPresent = false;
        for (var entry : entries) {
            if (matches(entry)) {
                expectedDataPresent = true;
            }
        }
        return new InstdataResourceStatus(entries.isEmpty(), expectedDataPresent);
    }

    private boolean matches(JsonNode entry) {
        return expectedRecord.norskident().equals(entry.path("norskident").asString())
                && expectedRecord.tssEksternId().equals(entry.path("tssEksternId").asString())
                && expectedRecord.institusjonstype().equals(entry.path("institusjonstype").asString())
                && expectedRecord.oppholdstype().equals(entry.path("oppholdstype").asString())
                && expectedRecord.startdato().toString().equals(entry.path("startdato").asString())
                && expectedRecord.forventetSluttdato().toString()
                .equals(entry.path("forventetSluttdato").asString())
                && expectedRecord.registrertAv().equals(entry.path("registrertAv").asString());
    }
}
