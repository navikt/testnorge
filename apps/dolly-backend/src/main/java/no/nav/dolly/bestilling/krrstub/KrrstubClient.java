package no.nav.dolly.bestilling.krrstub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.krrstub.dto.DigitalKontaktdataResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class KrrstubClient implements ClientRegister {

    private final KrrstubConsumer krrstubConsumer;
    private final MapperFacade mapperFacade;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    private static boolean isKrrMaalform(String spraak) {

        return isNotBlank(spraak) && Stream.of("NB", "NN", "EN", "SE").anyMatch(spraak::equalsIgnoreCase);
    }

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (nonNull(bestilling.getKrrstub()) ||
                (nonNull(bestilling.getTpsf()) && isKrrMaalform(bestilling.getTpsf().getSprakKode())) ||
                (nonNull(bestilling.getTpsMessaging()) && isKrrMaalform(bestilling.getTpsMessaging().getSpraakKode()))) {

            var context = new MappingContext.Factory().getContext();
            context.setProperty("ident", dollyPerson.getHovedperson());
            context.setProperty("bestilling", bestilling);

            var digitalKontaktdataRequest = mapperFacade.map(
                    nonNull(bestilling.getKrrstub()) ? bestilling.getKrrstub() : new RsDigitalKontaktdata(),
                    DigitalKontaktdata.class, context);

            return Flux.from(deleteKontaktdataPerson(dollyPerson.getHovedperson(), isOpprettEndre)
                    .flatMap(slettetStatus -> krrstubConsumer.createDigitalKontaktdata(digitalKontaktdataRequest))
                    .map(this::getStatus)
                    .map(status -> futurePersist(progress, status)));
        }

        return Flux.empty();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            progress.setKrrstubStatus(status);
            transactionHelperService.persister(progress);
            return progress;
        };
    }

    @Override
    public void release(List<String> identer) {

        krrstubConsumer.deleteKontaktdata(identer)
                .collectList()
                .subscribe(resp -> log.info("Slettet antall {} identer fra Krrstub", resp.size()));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getKrrstub()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> StringUtils.isNotBlank(entry.getKrrstubStatus()));
    }

    private Mono<DigitalKontaktdataResponse> deleteKontaktdataPerson(String ident, boolean opprettEndre) {

        return !opprettEndre ? krrstubConsumer.deleteKontaktdataPerson(ident)
                .map(status -> {
                    log.info("Kontaktdata slettet ident {} status {} ", ident, status.getStatus());
                    return status;
                }) :
                Mono.just(new DigitalKontaktdataResponse());
    }

    private String getStatus(DigitalKontaktdataResponse response) {

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(), response.getMelding());
    }
}
