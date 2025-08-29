package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
public class OppdaterPersonService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;
    private final BestillingProgressService bestillingProgressService;

    public OppdaterPersonService(
            IdentService identService,
            BestillingProgressService bestillingProgressService,
            BestillingService bestillingService,
            ObjectMapper objectMapper,
            MapperFacade mapperFacade,
            List<ClientRegister> clientRegisters,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            PdlDataConsumer pdlDataConsumer,
            TransactionHelperService transactionHelperService,
            PersonServiceClient personServiceClient,
            BestillingElasticRepository bestillingElasticRepository) {
        super(
                identService,
                bestillingService,
                objectMapper,
                mapperFacade,
                clientRegisters,
                counterCustomRegistry,
                pdlDataConsumer,
                errorStatusDecoder,
                transactionHelperService,
                bestillingElasticRepository
        );
        this.personServiceClient = personServiceClient;
        this.bestillingProgressService = bestillingProgressService;
    }

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        log.info("Bestilling med id=#{} med type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        request.setId(bestilling.getId());

        var testident = identService.getTestIdent(bestilling.getIdent());
        Flux.just(OriginatorUtility.prepOriginator(request, testident, mapperFacade))
                .flatMap(originator -> opprettProgress(bestilling, originator.getMaster(), testident.getIdent())
                        .flatMap(progress ->
                                oppdaterPdlPerson(originator, progress)
                                        .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                .filter(StringUtils::isNotBlank)
                                .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                        .flatMap(dollyPerson -> (!dollyPerson.getIdent().equals(bestilling.getIdent()) ?
                                                updateIdent(dollyPerson, progress) : Flux.just(ident))
                                                .doOnNext(nyident -> counterCustomRegistry.invoke(request))
                                                .flatMap(nyIdent -> Flux.concat(
                                                        gjenopprettKlienter(dollyPerson, request,
                                                                fase1Klienter(),
                                                                progress, true),
                                                        personServiceClient.syncPerson(dollyPerson, progress)
                                                                .map(ClientFuture::get)
                                                                .filter(BestillingProgress::isPdlSync)
                                                                .flatMap(pdlSync ->
                                                                        Flux.concat(
                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                        fase2Klienter(),
                                                                                        progress, true),
                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                        fase3Klienter(),
                                                                                        progress, true)))))))
                                .onErrorResume(throwable -> {
                                    var description = WebClientError.describe(throwable);
                                    var error = errorStatusDecoder.getErrorText(description.getStatus(), description.getMessage());
                                    log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                            progress.getId(), error, throwable);
                                    saveFeil(progress, error);
                                    return Flux.just(progress);
                                })))
                .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                .collectList()
                .doFinally(done -> {
                    doFerdig(bestilling);
                    saveBestillingToElasticServer(request, bestilling);
                    clearCache();
                })
                .subscribe();
    }



    private Flux<String> updateIdent(DollyPerson dollyPerson, BestillingProgress progress) {

        transactionHelperService.persister(progress, BestillingProgress::setIdent, dollyPerson.getIdent());
        identService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent()); // TODO: Check and fix blocking in non-blocking context.
        bestillingProgressService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent()); // TODO: Check and fix blocking in non-blocking context.
        bestillingService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent()); // TODO: Check and fix blocking in non-blocking context.

        return Flux.just(dollyPerson.getIdent());
    }
}
