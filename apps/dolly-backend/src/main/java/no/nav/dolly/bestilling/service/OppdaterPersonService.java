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
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

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
            PersonServiceClient personServiceClient) {
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
        this.mapperFacade = mapperFacade;
        this.bestillingProgressService = bestillingProgressService;
    }

    @Async
    public void oppdaterPersonAsync(RsDollyUpdateRequest request, Bestilling bestilling) {

        log.info("Bestilling med id=#{} med type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());

        var testident = identService.getTestIdent(bestilling.getIdent());
        Flux.just(OriginatorUtility.prepOriginator(request, testident, mapperFacade))
                .flatMap(originator -> opprettProgress(bestilling, originator.getMaster(), testident.getIdent())
                        .flatMap(progress -> (progress.isPdlf() ?
                                oppdaterPdlPerson(originator, testident.getIdent())
                                        .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse)) :
                                Flux.just(testident.getIdent()))
                                .flatMap(ident -> opprettDollyPerson(testident.getIdent(), progress, bestilling.getBruker())
                                        .flatMap(dollyPerson -> (!dollyPerson.getIdent().equals(bestilling.getIdent()) ?
                                                updateIdent(dollyPerson, progress) : Flux.just(ident))
                                                .doOnNext(nyident -> counterCustomRegistry.invoke(request))
                                                .flatMap(nyIdent -> Flux.concat(
                                                        gjenopprettKlienter(dollyPerson, request,
                                                                fase1Klienter(),
                                                                progress, true),
                                                        personServiceClient.syncPerson(dollyPerson, progress)
                                                                .map(ClientFuture::get)
                                                                .map(BestillingProgress::isPdlSync)
                                                                .flatMap(pdlSync -> isTrue(pdlSync) ?
                                                                        Flux.concat(
                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                        fase2Klienter(),
                                                                                        progress, true),
                                                                                gjenopprettKlienter(dollyPerson, request,
                                                                                        fase3Klienter(),
                                                                                        progress, true)) :
                                                                        Flux.empty())))))
                                .filter(Objects::nonNull)
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
                .subscribe(done -> doFerdig(bestilling));
    }

    private Flux<String> oppdaterPdlPerson(OriginatorUtility.Originator originator, String ident) {

        if (nonNull(originator.getPdlBestilling()) && nonNull(originator.getPdlBestilling().getPerson())) {

            return pdlDataConsumer.oppdaterPdl(ident,
                            PersonUpdateRequestDTO.builder()
                                    .person(originator.getPdlBestilling().getPerson())
                                    .build())
                    .doOnNext(response -> log.info("Oppdatert person til PDL-forvalter med response {}", response))
                    .map(response -> response.getStatus().is2xxSuccessful() ? response.getIdent() : "?");

        } else {
            return Flux.just(ident);
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
