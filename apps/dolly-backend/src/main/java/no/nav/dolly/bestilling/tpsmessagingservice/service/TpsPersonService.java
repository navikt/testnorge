package no.nav.dolly.bestilling.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsPersonService {

    private static final List<String> PENSJON_MILJOER = List.of("q1", "q2");
    private static final String TPS_SYNC_START = "Info: Synkronisering mot TPS startet ... %d ms";
    private static final long TIMEOUT_MILLIES = 835;
    private static final int MAX_SEKUNDER = 45;

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final TransactionHelperService transactionHelperService;

    public Flux<ClientFuture> syncPerson(DollyPerson dollyPerson, RsDollyBestillingRequest bestilling, BestillingProgress progress) {

        long startTime = System.currentTimeMillis();

        return Flux.from(Flux.fromIterable(bestilling.getEnvironments())
                .filter(PENSJON_MILJOER::contains)
                .filter(penMiljoe -> nonNull(bestilling.getPensjonforvalter()) &&
                        (nonNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                                nonNull(bestilling.getPensjonforvalter().getUforetrygd())))
                .collectList()
                .flatMap(penMiljoer -> getTpsPerson(LocalTime.now().plusSeconds(MAX_SEKUNDER), LocalTime.now(), dollyPerson.getIdent(),
                        penMiljoer, Collections.emptyList(), progress, new AtomicInteger(0))
                        .map(status -> prepareResult(dollyPerson.getIdent(), status, bestilling.getEnvironments(), startTime))
                        .map(status -> futurePersist(progress, status))));
    }

    private Mono<List<PersonMiljoeDTO>> getTpsPerson(LocalTime tidSlutt, LocalTime tidNo, String ident, List<String> miljoer,
                                                     List<PersonMiljoeDTO> status, BestillingProgress progress, AtomicInteger counter) {

        if (tidNo.isAfter(tidSlutt) ||
                (status.size() == miljoer.size() &&
                        status.stream().allMatch(PersonMiljoeDTO::isOk))) {
            return Mono.just(status);

        } else {

            transactionHelperService.persister(progress, BestillingProgress::setTpsSyncStatus,
                    miljoer.stream()
                            .map(miljoe -> String.format("%s:%s", miljoe, String.format(TPS_SYNC_START, counter.get() * TIMEOUT_MILLIES)))
                            .collect(Collectors.joining(",")));

            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT_MILLIES))
                    .flatMap(delayed -> tpsMessagingConsumer.getPerson(ident, miljoer))
                    .collectList()
                    .flatMap(resultat -> getTpsPerson(tidSlutt, LocalTime.now(), ident, miljoer, resultat, progress,
                            new AtomicInteger(counter.incrementAndGet())));
        }
    }

    private List<PersonMiljoeDTO> prepareResult(String ident, List<PersonMiljoeDTO> status, Set<String> miljoer, long startTime) {

        if (status.size() == miljoer.size()) {
            log.info("Synkronisering mot TPS for {} tok {} ms.", ident, System.currentTimeMillis() - startTime);

        } else {
            log.error("Synkronisering mot TPS for {} gitt opp etter {} ms.", ident, System.currentTimeMillis() - startTime);
        }

        return Stream.of(status, miljoer.stream()
                        .filter(miljoe -> status.stream().noneMatch(status1 -> status1.getMiljoe().equals(miljoe)))
                        .map(miljoe -> PersonMiljoeDTO.builder()
                                .miljoe(miljoe)
                                .status("NOK")
                                .melding(String.format("Feil: Synkronisering mot TPS gitt opp etter %d sekunder.", MAX_SEKUNDER))
                                .build())
                        .toList())
                .flatMap(Collection::stream)
                .toList();
    }

    private ClientFuture futurePersist(BestillingProgress progress, List<PersonMiljoeDTO> status) {

        return () -> {

            transactionHelperService.persister(progress, BestillingProgress::setTpsSyncStatus,
                    status.stream()
                            .map(detalj -> String.format("%s:%s", detalj.getMiljoe(),
                                    ErrorStatusDecoder.encodeStatus(detalj.isOk() ? detalj.getStatus() :
                                            StringUtils.trimToEmpty(String.format("%s %s", detalj.getMelding(), detalj.getUtfyllendeMelding())))))
                            .collect(Collectors.joining(",")));
            return progress;
        };
    }
}
