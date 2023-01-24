package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.IdentRepository.GruppeBestillingIdent;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.DollyPersonCache;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import org.slf4j.MDC;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class GjenopprettIdentService extends DollyBestillingService {

    public GjenopprettIdentService(DollyPersonCache dollyPersonCache, IdentService identService,
                                   BestillingProgressService bestillingProgressService,
                                   BestillingService bestillingService, MapperFacade mapperFacade,
                                   CacheManager cacheManager, ObjectMapper objectMapper,
                                   List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                   ErrorStatusDecoder errorStatusDecoder,
                                   PdlPersonConsumer pdlPersonConsumer, PdlDataConsumer pdlDataConsumer,
                                   TransactionHelperService transactionHelperService,
                                   PersonServiceClient personServiceClient) {

        super(dollyPersonCache, identService, bestillingProgressService,
                bestillingService, mapperFacade, cacheManager, objectMapper, clientRegisters, counterCustomRegistry,
                pdlPersonConsumer, pdlDataConsumer, errorStatusDecoder, transactionHelperService, personServiceClient);
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {
            bestKriterier.setEkskluderEksternePersoner(true);

            var coBestillinger = identService.getBestillingerFromIdent(bestilling.getIdent());
            var tIdent = identService.getTestIdent(bestilling.getIdent());

            if (isNull(tIdent)) {
                throw new NotFoundException(format("Fant ikke testident: %s i gruppe med id: %d", bestilling.getIdent(), bestilling.getGruppe().getId()));
            }

            Flux.just(tIdent)
                    .filter(testident -> !bestillingService.isStoppet(bestilling.getId()))
                    .flatMap(testident -> opprettProgress(bestilling, testident.getMaster())
                            .flatMap(progress -> sendOrdrePerson(progress, testident.getIdent())
                                    .flatMap(ident -> createDollyperson(progress, ident)
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, true),
                                                    personServiceClient.gjenopprett(null,
                                                                    dollyPerson, progress, false)
                                                            .map(ClientFuture::get)
                                                            .map(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync -> isTrue(pdlSync) ?
                                                                    Flux.fromIterable(coBestillinger)
                                                                            .filter(cobestilling -> ident.equals(cobestilling.getIdent()))
                                                                            .sort(Comparator.comparing(GruppeBestillingIdent::getBestillingid))
                                                                            .flatMap(cobestilling -> createBestilling(bestilling, cobestilling)
                                                                                    .flatMap(bestillingRequest -> Flux.concat(
                                                                                            gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                                    fase2Klienter(),
                                                                                                    progress, false),
                                                                                            gjenopprettKlienter(dollyPerson, bestillingRequest,
                                                                                                    fase3Klienter(),
                                                                                                    progress, false)))) :
                                                                    Flux.empty())
                                                            .filter(Objects::nonNull)))
                                            .onErrorResume(throwable -> {
                                                var error = errorStatusDecoder.getErrorText(
                                                        WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                                log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                        progress.getId(), error);
                                                progress.setFeil(error);
                                                transactionHelperService.persister(progress);
                                                return Flux.just(progress);
                                            }))))
                    .collectList()
                    .subscribe(done -> doFerdig(bestilling));
        }
    }
}
