package no.nav.dolly.bestilling.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.pdldata.dto.PdlResponse;
import no.nav.dolly.bestilling.personservice.PersonServiceClient;
import no.nav.dolly.bestilling.tpsmessagingservice.service.TpsPersonService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;

@Slf4j
@Service
public class OppdaterPersonService extends DollyBestillingService {

    private final PersonServiceClient personServiceClient;
    private final MapperFacade mapperFacade;
    private final BestillingProgressService bestillingProgressService;

    public OppdaterPersonService(
            IdentService identService,
            BestillingProgressService bestillingProgressService,
            BestillingService bestillingService,
            MapperFacade mapperFacade,
            ObjectMapper objectMapper,
            List<ClientRegister> clientRegisters,
            CounterCustomRegistry counterCustomRegistry,
            ErrorStatusDecoder errorStatusDecoder,
            PdlDataConsumer pdlDataConsumer,
            TransactionHelperService transactionHelperService,
            PersonServiceClient personServiceClient,
            TpsPersonService tpsPersonService) {
        super(
                identService,
                bestillingService,
                objectMapper,
                clientRegisters,
                counterCustomRegistry,
                pdlDataConsumer,
                errorStatusDecoder,
                transactionHelperService,
                tpsPersonService
        );
        this.personServiceClient = personServiceClient;
        this.mapperFacade = mapperFacade;
        this.bestillingProgressService = bestillingProgressService;
    }

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        log.info("Bestilling med id=#{} med type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        request.setId(bestilling.getId());

        var testident = identService.getTestIdent(bestilling.getIdent());
        Flux.just(OriginatorUtility.prepOriginator(request, testident, mapperFacade))
                .flatMap(originator -> opprettProgress(bestilling, originator.getMaster(), testident.getIdent())
                        .flatMap(progress -> (progress.isPdlf() ?
                                oppdaterPdlPerson(originator, progress)
                                        .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse)) :
                                Flux.just(testident.getIdent()))
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
                                                                                tpsPersonService.syncPerson(dollyPerson, request,
                                                                                                progress)
                                                                                        .map(ClientFuture::get),
                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                        fase2Klienter(),
                                                                                        progress, true),
                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                        fase3Klienter(),
                                                                                        progress, true)))))))
                                .onErrorResume(throwable -> {
                                    var error = errorStatusDecoder.getErrorText(
                                            WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                    log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                            progress.getId(), error, throwable);
                                    transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
                                    return Flux.just(progress);
                                })))
                .takeWhile(test -> !bestillingService.isStoppet(bestilling.getId()))
                .collectList()
                .doFinally(done -> doFerdig(bestilling))
                .subscribe();
    }

    private Flux<PdlResponse> oppdaterPdlPerson(OriginatorUtility.Originator originator, BestillingProgress progress) {

        if (nonNull(originator.getPdlBestilling()) && nonNull(originator.getPdlBestilling().getPerson())) {

            transactionHelperService.persister(progress, BestillingProgress::setPdlForvalterStatus,
                    "Info: Oppdatering av person startet ...");
            return pdlDataConsumer.oppdaterPdl(originator.getIdent(),
                            PersonUpdateRequestDTO.builder()
                                    .person(originator.getPdlBestilling().getPerson())
                                    .build())
                    .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response));

        } else {
            return Flux.just(PdlResponse.builder()
                    .ident(originator.getIdent())
                    .build());
        }
    }

    private Flux<String> updateIdent(DollyPerson dollyPerson, BestillingProgress progress) {

        transactionHelperService.persister(progress, BestillingProgress::setIdent, dollyPerson.getIdent());
        identService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent());
        bestillingProgressService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent());
        bestillingService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getIdent());

        return Flux.just(dollyPerson.getIdent());
    }
}
