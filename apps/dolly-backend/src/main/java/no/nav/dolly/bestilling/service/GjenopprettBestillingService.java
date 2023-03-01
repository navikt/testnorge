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
import no.nav.dolly.service.BestillingProgressService;
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

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class GjenopprettBestillingService extends DollyBestillingService {

    private final BestillingProgressService bestillingProgressService;
    private final PersonServiceClient personServiceClient;

    public GjenopprettBestillingService(
            IdentService identService,
            BestillingProgressService bestillingProgressService,
            BestillingService bestillingService,
            ObjectMapper objectMapper, List<ClientRegister> clientRegisters,
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
        this.bestillingProgressService = bestillingProgressService;
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

            var gamleProgresser = bestillingProgressService.fetchBestillingProgressByBestillingId(bestilling.getOpprettetFraId());

            Flux.fromIterable(gamleProgresser)
                    .flatMap(gmlProgress -> opprettProgress(bestilling, gmlProgress.getMaster(), gmlProgress.getIdent())
                            .flatMap(progress -> sendOrdrePerson(progress, gmlProgress.getIdent())
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, false),
                                                    personServiceClient.syncPerson(dollyPerson, progress)
                                                            .map(ClientFuture::get)
                                                            .map(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync -> isTrue(pdlSync) ?
                                                                    Flux.concat(
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase2Klienter(),
                                                                                    progress, false),
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase3Klienter(),
                                                                                    progress, false)) :
                                                                    Flux.empty())
                                                            .filter(Objects::nonNull)))
                                            .onErrorResume(throwable -> {
                                                var error = errorStatusDecoder.getErrorText(
                                                        WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                                log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                        progress.getId(), error, throwable);
                                                progress.setFeil(error);
                                                transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
                                                return Flux.just(progress);
                                            }))))
                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .collectList()
                    .doFinally(done -> doFerdig(bestilling))
                    .subscribe();

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }
}
