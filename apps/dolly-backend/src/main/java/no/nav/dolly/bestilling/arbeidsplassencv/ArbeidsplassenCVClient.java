package no.nav.dolly.bestilling.arbeidsplassencv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.PAMCVDTO;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsplassenCVClient implements ClientRegister {

    private final ArbeidsplassenCVConsumer arbeidsplassenCVConsumer;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(ordre -> nonNull(ordre.getArbeidsplassenCV()))
                .doOnNext(ordre ->
                        transactionHelperService.persister(progress, BestillingProgress::setArbeidsplassenCVStatus,
                                getInfoVenter("Arbeidsplassen")))
                .flatMap(ordre -> {
                    if (isOpprettEndre) {
                        var oppdatertOrdre = mapperFacade.map(ordre.getArbeidsplassenCV(), ArbeidsplassenCVDTO.class);
                        bestilling.setArbeidsplassenCV(oppdatertOrdre);
                        transactionHelperService.persister(progress.getBestilling().getId(), bestilling);
                        return Flux.just(oppdatertOrdre);
                    } else {
                        return Flux.just(ordre.getArbeidsplassenCV());
                    }
                })
                .flatMap(oppdatertOrdre -> Flux.just(CallIdUtil.generateCallId())
                        .flatMap(uuid -> arbeidsplassenCVConsumer.opprettPerson(dollyPerson.getIdent(), uuid)
                                .flatMap(response -> arbeidsplassenCVConsumer.godtaVilkaar(dollyPerson.getIdent(), uuid))
                                .flatMap(response2 -> {
                                    if (isTrue(bestilling.getArbeidsplassenCV().getHarHjemmel())) {
                                        return arbeidsplassenCVConsumer.godtaHjemmel(dollyPerson.getIdent(), uuid)
                                                .flatMap(response3 -> arbeidsplassenCVConsumer.godtaVilkaar(dollyPerson.getIdent(), uuid));
                                    } else {
                                        return Flux.just("Fortsetter uten hjemmel og repetert vilkaar");
                                    }
                                })
                                .flatMap(response4 -> Mono.just(mapperFacade.map(oppdatertOrdre, PAMCVDTO.class))
                                        .map(request -> arbeidsplassenCVConsumer.oppdaterCV(dollyPerson.getIdent(), request, uuid)
                                                .map(status -> status.getStatus().is2xxSuccessful() ? "OK" :
                                                        String.format("%s UUID: %s",
                                                                errorStatusDecoder.getErrorText(HttpStatus.valueOf(status.getStatus().value()),
                                                                        status.getFeilmelding()),
                                                                status.getUuid()))
                                                .map(resultat -> futurePersist(progress, resultat))))))
                .flatMap(Flux::from);
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setArbeidsplassenCVStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        arbeidsplassenCVConsumer.deleteCVer(identer)
                .collectList()
                .subscribe(response -> log.info("Sletting utført mot ArbeidsplassenCV"));
    }
}
