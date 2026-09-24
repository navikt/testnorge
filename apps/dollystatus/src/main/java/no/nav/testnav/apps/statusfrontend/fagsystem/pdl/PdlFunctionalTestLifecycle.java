package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.PdlTestLifecycle;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;

@Slf4j
@Service
public class PdlFunctionalTestLifecycle implements PdlTestLifecycle<PdlPreflight, PdlCreation> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("pdl"),
            new DisplayName("PDL"),
            Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2),
            CleanupExpectation.DELETED);

    private final PdlForvalterClient pdlForvalterClient;
    private final PdlProxyClient pdlProxyClient;
    private final PdlFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public PdlFunctionalTestLifecycle(
            PdlForvalterClient pdlForvalterClient,
            PdlProxyClient pdlProxyClient,
            PdlFunctionalTestProperties properties,
            Scheduler scheduler
    ) {
        this.pdlForvalterClient = pdlForvalterClient;
        this.pdlProxyClient = pdlProxyClient;
        this.properties = properties;
        this.scheduler = scheduler;
    }

    @Override
    public FunctionalTestDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Mono<PdlPreflight> preflight(FunctionalTestContext context) {
        return Mono.defer(pdlForvalterClient::personExists)
                .map(PdlPreflight::new);
    }

    @Override
    public Mono<PdlCreation> create(FunctionalTestContext context, PdlPreflight preflightResult) {
        var createPerson = preflightResult.personExists()
                ? Mono.just(false)
                : createPersonSafely()
                        .doOnError(throwable -> logFailure("opprett person", throwable))
                        .thenReturn(true);

        return createPerson
                .flatMap(personCreated -> Mono.defer(() -> personCreated
                                ? pdlForvalterClient.updateName()
                                : Mono.empty())
                        .doOnError(throwable -> logFailure("oppdater navn", throwable))
                        .then(Mono.defer(pdlForvalterClient::sendOrder)
                                .flatMap(this::verifyOrder)
                                .doOnError(throwable -> logFailure("send ordre", throwable)))
                        .thenReturn(new PdlCreation(personCreated)));
    }

    @Override
    public Mono<Void> verify(
            FunctionalTestContext context,
            PdlPreflight preflightResult,
            PdlCreation createResult,
            FunctionalTestEnvironment environment
    ) {
        return pollUntil(
                        () -> pdlProxyClient.personExists(environment, context.runId())
                                .doOnError(throwable -> logFailure("verifiser " + environment, throwable)),
                        Boolean.TRUE::equals,
                        properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler)
                .doOnError(throwable -> logFailure("poll verifiser " + environment, throwable))
                .then();
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            Optional<PdlPreflight> preflightResult,
            Optional<PdlCreation> createResult
    ) {
        if (preflightResult.isEmpty()) {
            return Mono.empty();
        }
        return Mono.defer(pdlForvalterClient::deletePerson);
    }

    private Mono<Void> createPersonSafely() {
        return Mono.defer(pdlForvalterClient::createPerson)
                .onErrorResume(this::isTimeout, _ -> recoverCreateTimeout())
                .then();
    }

    private Mono<Void> recoverCreateTimeout() {
        return Mono.defer(pdlForvalterClient::personExists)
                .flatMap(personExists -> personExists
                        ? Mono.empty()
                        : Mono.defer(pdlForvalterClient::createPerson));
    }

    private Mono<Void> verifyOrder(PdlOrderResponse response) {
        return hasFailedEvent(response)
                ? Mono.error(new IllegalStateException("PDL-ordren inneholder en feilstatus."))
                : Mono.empty();
    }

    private boolean hasFailedEvent(PdlOrderResponse response) {
        if (isNull(response)) {
            return true;
        }
        return Stream.concat(
                        Stream.ofNullable(response.hovedperson()),
                        safeList(response.relasjoner()).stream())
                .flatMap(personOrders -> safeList(personOrders.ordrer()).stream())
                .flatMap(order -> safeList(order.hendelser()).stream())
                .map(PdlOrderResponse.Event::status)
                .anyMatch("FEIL"::equals);
    }

    private boolean isTimeout(Throwable throwable) {
        return throwable instanceof TimeoutException
                || !isNull(throwable.getCause()) && isTimeout(throwable.getCause());
    }

    private void logFailure(String operation, Throwable throwable) {
        if (throwable instanceof WebClientResponseException responseException) {
            log.warn(
                    "PDL-livssyklusen feilet i operasjonen {} med HTTP-status {}.",
                    operation,
                    responseException.getStatusCode().value());
        } else {
            log.warn(
                    "PDL-livssyklusen feilet i operasjonen {} med feiltypen {}.",
                    operation,
                    throwable.getClass().getSimpleName());
        }
    }

    private <T> List<T> safeList(List<T> values) {
        return isNull(values) ? List.of() : values;
    }
}
