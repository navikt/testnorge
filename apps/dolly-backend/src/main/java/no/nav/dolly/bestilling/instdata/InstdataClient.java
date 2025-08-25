package no.nav.dolly.bestilling.instdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstdataClient implements ClientRegister {

    private final MapperFacade mapperFacade;
    private final InstdataConsumer instdataConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        var context = MappingContextUtils.getMappingContext();
        context.setProperty("ident", dollyPerson.getIdent());

        return Mono.just(bestilling.getInstdata())
                .filter(rsInstdata -> !rsInstdata.isEmpty())
                .flatMap(rsInstdata -> Mono.just(mapperFacade.mapAsList(rsInstdata, Instdata.class, context)))
                .flatMap(instdata -> instdataConsumer.getMiljoer()
                        .flatMap(miljoer -> Flux.fromIterable(miljoer)
                                .filter(miljoe -> bestilling.getEnvironments().contains(miljoe))
                                .flatMap(miljoe -> postInstdata(isOpprettEndre, instdata, miljoe))
                                .collect(Collectors.joining(",")))
                        .flatMap(status -> oppdaterStatus(progress, status)));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress,
                BestillingProgress::getInstdataStatus,
                BestillingProgress::setInstdataStatus, status);
    }

    @Override
    public void release(List<String> identer) {

        instdataConsumer.deleteInstdata(identer)
                .subscribe(response -> log.info("Slettet antall {} identer fra Instdata", response.size()));
    }

    private Mono<List<Instdata>> filterInstdata(List<Instdata> instdataRequest, String miljoe) {

        return instdataConsumer.getInstdata(instdataRequest.getFirst().getNorskident(), miljoe)
                .map(eksisterende -> {
                    log.info("Instdata hentet data fra {}: {}", miljoe, eksisterende.getInstitusjonsopphold());
                    return instdataRequest.stream()
                            .filter(request -> eksisterende.getInstitusjonsopphold()
                                    .getOrDefault(miljoe, emptyList())
                                    .stream()
                                    .noneMatch(request::equals))
                            .toList();
                });
    }

    private String getStatus(InstdataResponse response) {

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(),
                        response.getFeilmelding());
    }

    private Mono<String> postInstdata(boolean isNewOpphold, List<Instdata> instdata, String miljoe) {

        var oppholdId = new AtomicInteger(0);

        return (isNewOpphold ? Mono.just(instdata) : filterInstdata(instdata, miljoe))
                .flatMap(newInstdata -> {
                    if (!newInstdata.isEmpty()) {
                        return instdataConsumer.postInstdata(newInstdata, miljoe)
                                .map(response -> String.format("%s:opphold=%d$%s",
                                        miljoe, oppholdId.incrementAndGet(), getStatus(response)))
                                .collect(Collectors.joining(","));
                    } else {
                        return Mono.just(miljoe + ":opphold=1$OK");
                    }
                });
    }
}
