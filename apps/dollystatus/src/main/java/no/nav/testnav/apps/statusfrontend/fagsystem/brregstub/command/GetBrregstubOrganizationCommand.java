package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.stream.StreamSupport;

@RequiredArgsConstructor
public class GetBrregstubOrganizationCommand implements Callable<Mono<BrregstubResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final int organizationNumber;
    private final String expectedIdent;
    private final Duration timeout;

    @Override
    public Mono<BrregstubResourceStatus> call() {
        return webClient.get()
                .uri("/brregstub/api/v1/hentrolle/{organizationNumber}", organizationNumber)
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody().thenReturn(BrregstubResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus);
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<BrregstubResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private BrregstubResourceStatus toStatus(JsonNode response) {
        var roles = response.path("deltakere").path("roller");
        var expectedParticipants = roles.isArray()
                && !roles.isEmpty()
                && StreamSupport.stream(roles.spliterator(), false)
                .allMatch(role -> expectedIdent.equals(role.path("fodselsnr").asText()));
        return new BrregstubResourceStatus(false,
                response.path("orgnr").asInt() == organizationNumber && expectedParticipants);
    }
}
