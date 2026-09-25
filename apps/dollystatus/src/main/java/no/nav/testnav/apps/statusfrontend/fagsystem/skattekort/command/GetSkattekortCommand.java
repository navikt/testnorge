package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSkattekortCommand implements Callable<Mono<SkattekortResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final int incomeYear;
    private final FunctionalTestEnvironment environment;
    private final Duration timeout;

    @Override
    public Mono<SkattekortResourceStatus> call() {
        return webClient.post()
                .uri("/skattekort/{environment}/api/v1/person/hent-skattekort",
                        environment.name().toLowerCase())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBearerAuth(token))
                .bodyValue(Map.of("fnr", ident, "inntektsaar", incomeYear))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND
                            || response.statusCode() == HttpStatus.NO_CONTENT) {
                        return response.releaseBody().thenReturn(SkattekortResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus)
                                .defaultIfEmpty(SkattekortResourceStatus.emptyStatus());
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<SkattekortResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private SkattekortResourceStatus toStatus(JsonNode response) {
        if (!response.isArray()) {
            throw new IllegalStateException("Skattekortoppslaget returnerte ugyldig respons.");
        }
        for (var taxCard : response) {
            if (taxCard.path("inntektsaar").asInt() == incomeYear) {
                var result = taxCard.path("resultatForSkattekort").asString();
                return new SkattekortResourceStatus(
                        false,
                        "skattekortopplysningerOK".equals(result)
                                && hasExpectedTaxCard(taxCard),
                        "ikkeSkattekort".equals(result));
            }
        }
        return response.isEmpty()
                ? SkattekortResourceStatus.emptyStatus()
                : new SkattekortResourceStatus(false, false, false);
    }

    private boolean hasExpectedTaxCard(JsonNode taxCard) {
        var advanceTax = taxCard.path("forskuddstrekkList");
        if (!advanceTax.isArray() || advanceTax.size() != 1) {
            return false;
        }
        var deduction = advanceTax.get(0);
        return "loennFraNAV".equals(deduction.path("trekkode").asString())
                && deduction.path("frikort").path("frikortBeloep").asInt() == 50000;
    }
}
