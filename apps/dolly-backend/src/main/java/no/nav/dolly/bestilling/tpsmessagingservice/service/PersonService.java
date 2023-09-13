package no.nav.dolly.bestilling.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Service
@RequiredArgsConstructor
public class PersonService {

    private static final List<String> PENSJON_MILJOER = List.of("q1", "q2");
    private static final long TIMEOUT = 500;
    private static final int MAX_SEKUNDER = 30;

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final TransactionHelperService transactionHelperService;

    public Flux<ClientFuture> syncPerson(String ident, List<String> miljoer, RsDollyBestillingRequest bestilling) {

        var supportMiljoer = miljoer.stream()
                .filter(PENSJON_MILJOER::contains)
                .toList();

        if (!supportMiljoer.isEmpty() && nonNull(bestilling.getPensjonforvalter()) &&
                (nonNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                        nonNull(bestilling.getPensjonforvalter().getUforetrygd()))) {


            getTpsPerson(LocalTime.now().plusSeconds(MAX_SEKUNDER), LocalTime.now(), ident, supportMiljoer, Collections.emptyList());


        }
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

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress,
                                       List<PersonServiceResponse> status) {

        return () -> {
            status.stream()
                    .filter(entry -> dollyPerson.getIdent().equals(entry.getIdent()))
                    .forEach(entry -> {
                        progress.setPdlSync(entry.getStatus().is2xxSuccessful() && isTrue(entry.getExists()));
                        if (!dollyPerson.isOrdre()) {
                            transactionHelperService.persister(progress, BestillingProgress::setPdlPersonStatus, entry.getFormattertMelding());
                        }
                    });
            return progress;
        };
    }
}
