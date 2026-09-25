package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.TpsEgenansattResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetEgenansattCommand implements Callable<Mono<TpsEgenansattResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final List<String> environments;
    private final LocalDate expectedFromDate;
    private final Duration timeout;

    @Override
    public Mono<TpsEgenansattResourceStatus> call() {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/personer/ident")
                        .queryParam("miljoer", environments)
                        .build())
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(new PersonRequest(ident))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::toStatus)
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private TpsEgenansattResourceStatus toStatus(JsonNode response) {
        if (!response.isArray()) {
            throw new IllegalStateException("TPS-oppslaget returnerte ugyldig respons.");
        }
        var returnedEnvironments = new HashSet<String>();
        var expectedActiveEnvironments = new HashSet<String>();
        var inactiveEnvironments = new HashSet<String>();
        for (var environmentStatus : response) {
            var environment = environmentStatus.path("miljoe").asString();
            if (!environments.contains(environment)
                    || !"OK".equals(environmentStatus.path("status").asString())) {
                continue;
            }
            returnedEnvironments.add(environment);
            var person = environmentStatus.path("person");
            var fromDate = readDate(person.path("egenAnsattDatoFom"));
            var toDate = readDate(person.path("egenAnsattDatoTom"));
            if (expectedFromDate.equals(fromDate) && toDate == null) {
                expectedActiveEnvironments.add(environment);
            }
            if (fromDate == null || toDate != null) {
                inactiveEnvironments.add(environment);
            }
        }
        return new TpsEgenansattResourceStatus(
                returnedEnvironments.containsAll(environments),
                expectedActiveEnvironments.containsAll(environments),
                inactiveEnvironments.containsAll(environments));
    }

    private LocalDate readDate(JsonNode value) {
        var text = value.asString();
        if (text.length() < 10) {
            return null;
        }
        return LocalDate.parse(text.substring(0, 10));
    }

    private record PersonRequest(String ident) {
    }
}
