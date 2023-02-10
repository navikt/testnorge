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
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.CounterCustomRegistry;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.util.CurrentAuthentication;
import no.nav.dolly.util.ThreadLocalContextLifter;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.jpa.Testident.Master.PDLF;
import static no.nav.dolly.util.MdcUtil.MDC_KEY_BESTILLING;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
public class OpprettPersonerByKriterierService extends DollyBestillingService {

    private PersonServiceClient personServiceClient;
    private MapperFacade mapperFacade;
    private GetUserInfo getUserInfo;

    public OpprettPersonerByKriterierService(IdentService identService,
                                             BestillingService bestillingService,
                                             ObjectMapper objectMapper, MapperFacade mapperFacade,
                                             List<ClientRegister> clientRegisters,
                                             CounterCustomRegistry counterCustomRegistry,
                                             ErrorStatusDecoder errorStatusDecoder,
                                             PdlDataConsumer pdlDataConsumer,
                                             TransactionHelperService transactionHelperService,
                                             PersonServiceClient personServiceClient,
                                             GetUserInfo getUserInfo) {

        super(identService, bestillingService, objectMapper, clientRegisters, counterCustomRegistry,
                pdlDataConsumer, errorStatusDecoder, transactionHelperService);

        this.personServiceClient = personServiceClient;
        this.mapperFacade = mapperFacade;
        this.getUserInfo = getUserInfo;
    }

    @Async
    public void executeAsync(Bestilling bestilling) {

        log.info("Bestilling med id=#{} og type={} er startet ...", bestilling.getId(), getBestillingType(bestilling));
        MDC.put(MDC_KEY_BESTILLING, bestilling.getId().toString());
        Hooks.onEachOperator(Operators.lift(new ThreadLocalContextLifter<>()));

        var bestKriterier = getDollyBestillingRequest(bestilling);
        var userInfo = Optional
                .ofNullable(bestilling.getBruker())
                .orElseGet(() -> CurrentAuthentication.getAuthUser(getUserInfo));

        if (nonNull(bestKriterier)) {

            var originator = OriginatorUtility.prepOriginator(bestKriterier, mapperFacade);

            Flux.range(0, bestilling.getAntallIdenter())
                    .filter(index -> !bestillingService.isStoppet(bestilling.getId()))
                    .flatMap(index -> opprettProgress(bestilling, PDLF)
                            .flatMap(progress -> opprettPerson(originator)
                                    .flatMap(pdlResponse -> sendOrdrePerson(progress, pdlResponse))
                                    .filter(Objects::nonNull)
                                    .flatMap(ident -> opprettDollyPerson(ident, progress, userInfo)
                                            .doOnNext(dollyPerson -> leggIdentTilGruppe(ident, progress,
                                                    bestKriterier.getBeskrivelse()))
                                            .doOnNext(dollyPerson -> counterCustomRegistry.invoke(bestKriterier))
                                            .flatMap(dollyPerson -> Flux.concat(
                                                    gjenopprettKlienter(dollyPerson, bestKriterier,
                                                            fase1Klienter(),
                                                            progress, true),
                                                    personServiceClient.syncPerson(dollyPerson, progress)
                                                            .map(ClientFuture::get)
                                                            .map(BestillingProgress::isPdlSync)
                                                            .flatMap(pdlSync -> isTrue(pdlSync) ?
                                                                    Flux.concat(
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase2Klienter(),
                                                                                    progress, true),
                                                                            gjenopprettKlienter(dollyPerson, bestKriterier,
                                                                                    fase3Klienter(),
                                                                                    progress, true)) :
                                                                    Flux.empty())
                                                            .filter(Objects::nonNull))))
                                    .onErrorResume(throwable -> {
                                        var error = errorStatusDecoder.getErrorText(
                                                WebClientFilter.getStatus(throwable), WebClientFilter.getMessage(throwable));
                                        log.error("Feil oppsto ved utføring av bestilling, progressId {} {}",
                                                progress.getId(), error, throwable);
                                        transactionHelperService.persister(progress, BestillingProgress::setFeil, error);
                                        return Flux.just(progress);
                                    })))
                    .collectList()
                    .subscribe(done -> doFerdig(bestilling));

        } else {
            bestilling.setFeil("Feil: kunne ikke mappe JSON request, se logg!");
            doFerdig(bestilling);
        }
    }
}
