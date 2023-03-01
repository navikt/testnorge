package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;

@Slf4j
@Service
public class GjenopprettGruppeService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public GjenopprettGruppeService(
            IdentService identService,
            BestillingService bestillingService,
            ObjectMapper objectMapper,
            List<ClientRegister> clientRegisters,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            PdlDataConsumer pdlDataConsumer,
            TransactionHelperService transactionHelperService,
            PersonServiceClient personServiceClient
    ) {
        super(
                identService,
                bestillingService,
                objectMapper,
                clientRegisters,
                counterCustomRegistry,
                pdlDataConsumer,
                errorStatusDecoder,
                transactionHelperService
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var coBestillinger = identService.getBestillingerFromGruppe(bestilling.getGruppe());

            var emptyBestillingCounter = new ConcurrentHashMap<String, Boolean>();
            Flux.fromIterable(bestilling.getGruppe().getTestidenter())
                    .delaySequence(Duration.ofMillis(200))
                    .flatMap(testident -> opprettProgress(bestilling, testident.getMaster(), testident.getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress, testident.getIdent())
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, true),
                                                    personServiceClient.syncPerson(dollyPerson, progress)
                                                            .map(ClientFuture::get)
                                                            .filter(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync -> Flux.fromIterable(coBestillinger)
                                                                            .sort(Comparator.comparing(GruppeBestillingIdent::getBestillingid))
                                                                            .filter(cobestilling -> ident.equals(cobestilling.getIdent()))
                                                                            .filter(cobestilling ->
                                                                                    isNotTrue(emptyBestillingCounter.putIfAbsent(ident, true)) ||
                                                                                            (nonNull(cobestilling.getBestkriterier()) &&
                                                                                                    !cobestilling.getBestkriterier().equals("{}")))
                                                                            .flatMap(cobestilling -> createBestilling(bestilling, cobestilling)
                                                                                    .flatMap(bestillingRequest -> Flux.concat(
                                                                                            gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                                    fase2Klienter(),
                                                                                                    progress, false),
                                                                                            gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                                    fase3Klienter(),
                                                                                                    progress, false)))))))
                                            .onErrorResume(throwable -> {
                                                var error = errorStatusDecoder.getErrorText(
                                                        WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                                log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                        progress.getId(), error, throwable);
                                                transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
                                                return Flux.just(progress);
                                            }))))
                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .collectList()
                    .doFinally(done -> doFerdig(bestilling))
                    .subscribe();
        }
    }
}
