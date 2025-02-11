package no.nav.dolly.bestilling.arbeidssoekerregisteret;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretRequest;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidssoekerregisteretClient implements ClientRegister {

    private final ArbeidssoekerregisteretConsumer arbeidssoekerregisteretConsumer;
    private final TransactionHelperService transactionHelperService;
    private final MapperFacade mapperFacade;


    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(bestilling1 -> nonNull(bestilling1.getArbeidssoekerregisteret()))
                .map(RsDollyUtvidetBestilling::getArbeidssoekerregisteret)
                .map(soknad -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    return mapperFacade.map(soknad, ArbeidssoekerregisteretRequest.class, context);
                })
                .map(arbeidssoekerregisteretConsumer::postArbeidssokerregisteret)
                .flatMap(response -> response.map(arbeidssokerregisteretResponse -> {
                    log.info("Mottatt respons fra arbeidssøkerregisteret: {}, feilmelding: {}", arbeidssokerregisteretResponse.getStatus(),
                            arbeidssokerregisteretResponse.getFeilmelding());
                    if (arbeidssokerregisteretResponse.getStatus().is2xxSuccessful()) {
                        return "OK";
                    } else {
                        return "FEIL=" + ErrorStatusDecoder.encodeStatus(arbeidssokerregisteretResponse.getFeilmelding());
                    }
                }))
                .map(status -> futurePersist(progress, status));
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress,
                    BestillingProgress::getArbeidssoekerregisteretStatus,
                    BestillingProgress::setArbeidssoekerregisteretStatus, status);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        Flux.fromIterable(identer)
                .flatMap(arbeidssoekerregisteretConsumer::deleteArbeidssokerregisteret)
                .collectList()
                .subscribe(response -> log.info("Slettet {} fra arbeidssøkerregisteret", response.size()));
    }
}
