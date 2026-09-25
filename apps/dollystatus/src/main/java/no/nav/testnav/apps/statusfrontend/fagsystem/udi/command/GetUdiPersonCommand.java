package no.nav.testnav.apps.statusfrontend.fagsystem.udi.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiResourceStatus;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetUdiPersonCommand implements Callable<Mono<UdiResourceStatus>> {

    private final WebClient webClient;
    private final String token;
    private final UdiRequest expectedRequest;
    private final Duration timeout;

    @Override
    public Mono<UdiResourceStatus> call() {
        return webClient.get()
                .uri("/udistub/api/v1/person/{ident}", expectedRequest.ident())
                .headers(headers -> headers.setBearerAuth(token))
                .exchangeToMono(response -> {
                    if (response.statusCode() == HttpStatus.NOT_FOUND) {
                        return response.releaseBody().thenReturn(UdiResourceStatus.emptyStatus());
                    }
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(this::toStatus)
                                .switchIfEmpty(Mono.error(new IllegalStateException(
                                        "UDI-oppslaget returnerte tom respons.")));
                    }
                    return response.createException()
                            .flatMap(exception -> Mono.<UdiResourceStatus>error(exception));
                })
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }

    private UdiResourceStatus toStatus(JsonNode response) {
        var person = response.path("person");
        if (!person.isObject()) {
            throw new IllegalStateException("UDI-oppslaget returnerte ugyldig respons.");
        }
        var name = person.path("navn");
        var expectedName = expectedRequest.navn();
        var expectedDataPresent = expectedRequest.ident().equals(person.path("ident").asString())
                && expectedName.fornavn().equals(name.path("fornavn").asString())
                && expectedName.etternavn().equals(name.path("etternavn").asString())
                && expectedRequest.foedselsDato().toString().equals(
                person.path("foedselsDato").asString())
                && expectedRequest.avgjoerelseUavklart().equals(
                person.path("avgjoerelseUavklart").asBoolean())
                && expectedRequest.harOppholdsTillatelse().equals(
                person.path("harOppholdsTillatelse").asBoolean())
                && expectedRequest.flyktning().equals(person.path("flyktning").asBoolean())
                && expectedRequest.soeknadOmBeskyttelseUnderBehandling().equals(
                person.path("soeknadOmBeskyttelseUnderBehandling").asString())
                && expectedRequest.soknadDato().toString().equals(
                person.path("soknadDato").asString());
        return new UdiResourceStatus(false, expectedDataPresent);
    }
}
