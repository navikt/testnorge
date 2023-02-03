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
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingProgressService;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonUpdateRequestDTO;
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
public class LeggTilPaaGruppeService extends DollyBestillingService {

    private PersonServiceClient personServiceClient;
    private MapperFacade mapperFacade;
    private BestillingProgressService bestillingProgressService;

    public LeggTilPaaGruppeService(IdentService identService,
                                   BestillingProgressService bestillingProgressService,
                                   BestillingService bestillingService, MapperFacade mapperFacade,
                                   ObjectMapper objectMapper,
                                   List<ClientRegister> clientRegisters, CounterCustomRegistry counterCustomRegistry,
                                   ErrorStatusDecoder errorStatusDecoder,
                                   PdlDataConsumer pdlDataConsumer,
                                   TransactionHelperService transactionHelperService,
                                   PersonServiceClient personServiceClient) {

        super(identService, bestillingService, objectMapper, clientRegisters, counterCustomRegistry,
                pdlDataConsumer, errorStatusDecoder, transactionHelperService);

        this.personServiceClient = personServiceClient;
        this.mapperFacade = mapperFacade;
        this.bestillingProgressService = bestillingProgressService;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        RsDollyBestillingRequest bestKriterier = getDollyBestillingRequest(bestilling);

        if (nonNull(bestKriterier)) {

            Flux.fromIterable(bestilling.getGruppe().getTestidenter())
                    .flatMap(testident -> Flux.just(new OriginatorCommand(bestKriterier, testident, mapperFacade).call())
                            .flatMap(originator -> opprettProgress(bestilling, originator.getMaster())
                                    .flatMap(progress -> (originator.isPdlf() ?
                                            oppdaterPdlPerson(originator, testident.getIdent())
                                                    .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse)) :
                                            Flux.just(testident.getIdent()))
                                            .flatMap(ident -> Flux.just(DollyPerson.preparePerson(testident, bestilling.getGruppe().getTags()))
                                                    .flatMap(dollyPerson -> (!dollyPerson.getHovedperson().equals(bestilling.getIdent()) ?
                                                            updateIdent(dollyPerson, progress) : Flux.just(ident))
                                                            .doOnNext(nyident -> counterCustomRegistry.invoke(bestKriterier))
                                                            .flatMap(nyIdent -> Flux.concat(
                                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                            fase1Klienter(),
                                                                            progress, true),
                                                                    personServiceClient.syncPerson(dollyPerson, progress)
                                                                            .map(ClientFuture::get)
                                                                            .map(BestillingProgress::isPdlSync)
                                                                            .flatMap(pdlSync -> (isTrue(pdlSync) ?
                                                                                    Flux.concat(
                                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                                    fase2Klienter(),
                                                                                                    progress, true),
                                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                                    fase3Klienter(),
                                                                                                    progress, true)) :
                                                                                    Flux.empty())))))
                                                    .filter(Objects::nonNull)
                                                    .onErrorResume(throwable -> {
                                                        var error = errorStatusDecoder.getErrorText(
                                                                WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                                        log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                                progress.getId(), error);
                                                        bestilling.setFeil(error);
                                                        return Flux.just(progress);
                                                    })))))
                    .collectList()
                    .subscribe(done -> doFerdig(bestilling));
        }
    }

    private Flux<String> oppdaterPdlPerson(OriginatorCommand.Originator originator, String ident) {

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

        progress.setIdent(dollyPerson.getHovedperson());
        transactionHelperService.persister(progress);
        identService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getHovedperson());
        bestillingProgressService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getHovedperson());
        bestillingService.swapIdent(progress.getBestilling().getIdent(), dollyPerson.getHovedperson());

        return Flux.just(dollyPerson.getHovedperson());
    }
}
