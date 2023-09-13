package no.nav.dolly.bestilling.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
public class TpsPersonService {

    private static final List<String> PENSJON_MILJOER = List.of("q1", "q2");
    private static final String TPS_SYNC_START = "Info: Synkronisering mot TPS startet ...";
    private static final long TIMEOUT = 500;
    private static final int MAX_SEKUNDER = 30;

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final TransactionHelperService transactionHelperService;

    public Flux<ClientFuture> syncPerson(String ident, List<String> miljoer, RsDollyBestillingRequest bestilling, BestillingProgress progress) {

        return Flux.from(Flux.fromIterable(miljoer)
                .filter(PENSJON_MILJOER::contains)
                .filter(penMiljoer -> nonNull(bestilling.getPensjonforvalter()) &&
                        (nonNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                                nonNull(bestilling.getPensjonforvalter().getUforetrygd())))
                .collectList()
                .doOnNext(penMiljoer -> transactionHelperService.persister(progress, BestillingProgress::setTpsSyncStatus, TPS_SYNC_START))
                .flatMap(penMiljoer -> getTpsPerson(LocalTime.now().plusSeconds(MAX_SEKUNDER), LocalTime.now(), ident,
                        penMiljoer, Collections.emptyList())
                        .map(status -> futurePersist(progress, status))));
    }

    private Mono<List<PersonMiljoeDTO>> getTpsPerson(LocalTime tidSlutt, LocalTime tidNo, String ident, List<String> miljoer,
                                                     List<PersonMiljoeDTO> status) {

        if (tidNo.isAfter(tidSlutt) ||
                (status.size() == miljoer.size() &&
                        status.stream().allMatch(PersonMiljoeDTO::isOk))) {
            return Mono.just(status);

        } else {
            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT))
                    .flatMap(delayed -> tpsMessagingConsumer.getPerson(ident, miljoer))
                    .collectList()
                    .flatMap(resultat -> getTpsPerson(tidSlutt, LocalTime.now(), ident, miljoer, resultat));
        }
    }

    private ClientFuture futurePersist(BestillingProgress progress, List<PersonMiljoeDTO> status) {

        return () -> {
            progress.setTpsSync(status.stream()
                    .allMatch(PersonMiljoeDTO::isOk));

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
