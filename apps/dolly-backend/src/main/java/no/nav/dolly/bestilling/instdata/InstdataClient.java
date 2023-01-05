package no.nav.dolly.bestilling.instdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.domain.resultset.tpsf.DollyPerson;
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
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstdataClient implements ClientRegister {

    private final MapperFacade mapperFacade;
    private final InstdataConsumer instdataConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;

    @Override
    public Flux<Void> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (!bestilling.getInstdata().isEmpty()) {

            try {
                var test = instdataConsumer.getInstdataTest(dollyPerson.getHovedperson(), "q2").block();
                log.info(test.getBody().textValue());

            } catch (RuntimeException e) {
                log.error("En feil oppsto {}", e.getMessage(), e);
            }

            var context = MappingContextUtils.getMappingContext();
            context.setProperty("ident", dollyPerson.getHovedperson());
            var instdata = mapperFacade.mapAsList(bestilling.getInstdata(), Instdata.class, context);

            instdataConsumer.getMiljoer()
                    .flatMap(miljoer -> Flux.fromIterable(miljoer)
                            .filter(miljoe -> bestilling.getEnvironments().contains(miljoe))
                            .flatMap(miljoe -> postInstdata(isOpprettEndre, instdata, miljoe))
                            .collect(Collectors.joining(",")))
                    .subscribe(resultat -> {
                        progress.setInstdataStatus(resultat);
                        transactionHelperService.persister(progress);
                    });
        }
        return Flux.just();
    }

    @Override
    public void release(List<String> identer) {

        instdataConsumer.deleteInstdata(identer)
                .subscribe(response -> log.info("Slettet antall {} identer fra Instdata", response.size()));
    }

    @Override
    public boolean isDone(RsDollyBestilling kriterier, Bestilling bestilling) {

        return isNull(kriterier.getInstdata()) ||
                bestilling.getProgresser().stream()
                        .allMatch(entry -> isNotBlank(entry.getInstdataStatus()));
    }

    private Mono<List<Instdata>> filterInstdata(List<Instdata> instdataRequest, String miljoe) {

        return instdataConsumer.getInstdata(instdataRequest.get(0).getNorskident(), miljoe)
                .map(eksisterende -> {
                    log.info("Instdata hentet data fra {}: {}", miljoe, eksisterende.getInstitusjonsopphold());
                    return instdataRequest.stream()
                            .filter(request -> eksisterende.getInstitusjonsopphold()
                                    .getOrDefault(miljoe, emptyList()).stream()
                                    .noneMatch(opphold -> request.equals(opphold)))
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
