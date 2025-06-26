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
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;

@Slf4j
@Service
public class OpprettPersonerFraIdenterMedKriterierService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;

    public OpprettPersonerFraIdenterMedKriterierService(
            BestillingElasticRepository bestillingElasticRepository,
            BestillingProgressRepository bestillingProgressRepository,
            BestillingRepository bestillingRepository,
            BestillingService bestillingService,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            IdentService identService,
            List<ClientRegister> clientRegisters,
            MapperFacade mapperFacade,
            ObjectMapper objectMapper,
            PdlDataConsumer pdlDataConsumer,
            PersonServiceClient personServiceClient,
            TestgruppeRepository testgruppeRepository,
            TransactionHelperService transactionHelperService
    ) {
        super(
                bestillingElasticRepository,
                bestillingProgressRepository,
                bestillingRepository,
                bestillingService,
                counterCustomRegistry,
                errorStatusDecoder,
                identService,
                clientRegisters,
                mapperFacade,
                objectMapper,
                pdlDataConsumer,
                testgruppeRepository,
                transactionHelperService
        );
        this.personServiceClient = personServiceClient;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);
        if (nonNull(bestKriterier)) {

            new AvailCheckCommand(bestilling.getOpprettFraIdenter(), pdlDataConsumer).call()
                    .filter(AvailCheckCommand.AvailStatus::isAvailable)
                    .map(AvailCheckCommand.AvailStatus::getIdent)
                    .flatMap(availIdent -> Flux.just(OriginatorUtility.prepOriginator(bestKriterier,
                                    availIdent, mapperFacade))
                            .flatMap(originator -> opprettProgress(bestilling, PDLF, availIdent)
                                    .flatMap(progress -> opprettPerson(originator, progress)
                                            .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                            .filter(StringUtils::isNotBlank)
                                            .flatMap(ident -> opprettDollyPerson(ident, progress, bestilling.getBruker())
                                                    .doOnNext(dollyPerson ->
                                                            leggIdentTilGruppe(ident, progress, bestKriterier.getBeskrivelse()))
                                                    .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                                    .flatMap(dollyPerson -> Flux.concat(
                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                    fase1Klienter(),
                                                                    progress, true),
                                                            personServiceClient.syncPerson(dollyPerson, progress)
                                                                    .map(ClientFuture::get)
                                                                    .filter(BestillingProgress::isPdlSync)
                                                                    .flatMap(pdlSync -> Flux.concat(
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase2Klienter(),
                                                                                    progress, true),
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase3Klienter(),
                                                                                    progress, true))))))
                                            .onErrorResume(throwable -> {
                                                var description = WebClientError.describe(throwable);
                                                var error = errorStatusDecoder.getErrorText(description.getStatus(), description.getMessage());
                                                log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                        progress.getId(), error, throwable);
                                                saveFeil(progress, error);
                                                return Flux.just(progress);
                                            }))))
                    .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                    .collectList()
                    .doFinally(done -> {
                        doFerdig(bestilling);
                        saveBestillingToElasticServer(bestKriterier, bestilling);
                        clearCache();
                    })
                    .subscribe();

        } else {

            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }
}
