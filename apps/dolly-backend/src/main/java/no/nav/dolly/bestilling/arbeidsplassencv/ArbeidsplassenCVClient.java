package no.nav.dolly.bestilling.arbeidsplassencv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Objects.nonNull;

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
                .flatMap(ordre -> arbeidsplassenCVConsumer.hentPerson(dollyPerson.getIdent())
                        .flatMap(response -> arbeidsplassenCVConsumer.opprettPerson(dollyPerson.getIdent())
                                .flatMap(response2 -> arbeidsplassenCVConsumer.opprettSamtykke(dollyPerson.getIdent())
                                        .flatMap(response3 -> Mono.just(mapperFacade.map(ordre.getArbeidsplassenCV(), ArbeidsplassenCVDTO.class))
                                                .map(request -> arbeidsplassenCVConsumer.oppdaterCV(dollyPerson.getIdent(), request)
                                                        .map(status -> status.getStatus().is2xxSuccessful() ? "OK" :
                                                                errorStatusDecoder.getErrorText(HttpStatus.valueOf(status.getStatus().value()),
                                                                        status.getFeilmelding()))
                                                        .map(resultat -> futurePersist(progress, resultat)))))))
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

//        arbeidplassenCVConsumer.deleteCVer(identer)
//                .collectList()
//                .subscribe(response -> log.info("Slettet utført mot ArbeidsplassenCV"));
    }
}
