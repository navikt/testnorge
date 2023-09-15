package no.nav.dolly.bestilling.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsPersonService {

    private static final List<String> PENSJON_MILJOER = List.of("q1", "q2");
    private static final String TPS_SYNC_START = "Info: Synkronisering mot TPS startet ... %d ms";
    private static final long TIMEOUT_MILLIES = 839;
    private static final long MAX_MILLIES = 45_000;

    private final TpsMessagingConsumer tpsMessagingConsumer;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingService transaksjonMappingService;

    public Flux<ClientFuture> syncPerson(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestilling, BestillingProgress progress, boolean isOpprettEndre) {

        long startTime = System.currentTimeMillis();

        return Flux.from(Flux.fromIterable(bestilling.getEnvironments())
                .filter(PENSJON_MILJOER::contains)
                .collectList()
                .filter(penMiljoer -> !penMiljoer.isEmpty())
                .filter(penMiljoer -> isRelevantBestilling(bestilling) &&
                        (isOpprettEndre || !isTransaksjonMapping(dollyPerson.getIdent(), bestilling, penMiljoer)))
                .flatMap(penMiljoer -> getTpsPerson(startTime, dollyPerson.getIdent(),
                        penMiljoer, Collections.emptyList(), progress))
                .map(status -> prepareResult(dollyPerson.getIdent(), status, bestilling.getEnvironments(), startTime))
                .map(status -> futurePersist(progress, status)));
    }

    private boolean isRelevantBestilling(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getPensjonforvalter()) &&
                (nonNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                        nonNull(bestilling.getPensjonforvalter().getUforetrygd()));
    }

    private boolean isTransaksjonMapping(String ident, RsDollyUtvidetBestilling bestilling, List<String> miljoer) {

        var transaksjoner = transaksjonMappingService.getTransaksjonMapping(ident);

        return (isNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                transaksjoner.stream()
                        .anyMatch(transaksjon -> miljoer.stream()
                                .allMatch(miljoe -> SystemTyper.PEN_AP.name().equals(transaksjon.getSystem()) &&
                                        miljoe.equals(transaksjon.getMiljoe())))) &&

                (isNull(bestilling.getPensjonforvalter().getUforetrygd()) ||
                        transaksjoner.stream()
                                .anyMatch(transaksjon2 -> miljoer.stream()
                                        .allMatch(miljoe -> SystemTyper.PEN_UT.name().equals(transaksjon2.getSystem()) &&
                                                miljoe.equals(transaksjon2.getMiljoe()))));
    }

    private Mono<List<PersonMiljoeDTO>> getTpsPerson(Long starttid, String ident, List<String> miljoer,
                                                     List<PersonMiljoeDTO> status, BestillingProgress progress) {

        if (System.currentTimeMillis() - (starttid + MAX_MILLIES) > 0 ||
                (status.size() == miljoer.size() &&
                        status.stream().allMatch(PersonMiljoeDTO::isOk))) {
            return Mono.just(status);

        } else {

            transactionHelperService.persister(progress, BestillingProgress::setTpsSyncStatus,
                    miljoer.stream()
                            .map(miljoe -> String.format("%s:%s", miljoe, String.format(TPS_SYNC_START,
                                    System.currentTimeMillis() - starttid)))
                            .collect(Collectors.joining(",")));

            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT_MILLIES))
                    .flatMap(delayed -> tpsMessagingConsumer.getPerson(ident, miljoer))
                    .collectList()
                    .flatMap(resultat -> getTpsPerson(starttid, ident, miljoer, resultat, progress));
        }
    }

    private List<PersonMiljoeDTO> prepareResult(String ident, List<PersonMiljoeDTO> status, Set<String> miljoer, long startTime) {

        if (status.size() == miljoer.size()) {
            log.info("Synkronisering mot TPS for {} tok {} ms.", ident, System.currentTimeMillis() - startTime);

        } else {
            log.warn("Synkronisering mot TPS for {} gitt opp etter {} ms.", ident, System.currentTimeMillis() - startTime);
        }

        return Stream.of(status.stream()
                                .filter(status1 -> StringUtils.isNotBlank(status1.getMiljoe())),
                        miljoer.stream()
                                .filter(miljoe -> status.stream().noneMatch(status1 -> miljoe.equals(status1.getMiljoe())))
                                .map(miljoe -> PersonMiljoeDTO.builder()
                                        .miljoe(miljoe)
                                        .status("NOK")
                                        .utfyllendeMelding(String.format("Feil: Synkronisering mot TPS gitt opp etter %d sekunder.", MAX_MILLIES/1000))
                                        .build()))
                .flatMap(Function.identity())
                .toList();
    }

    private ClientFuture futurePersist(BestillingProgress progress, List<PersonMiljoeDTO> status) {

        return () -> {

            progress.setIsTpsSyncEnv(status.stream()
                    .filter(PersonMiljoeDTO::isOk)
                    .map(PersonMiljoeDTO::getMiljoe)
                    .toList());

            transactionHelperService.persister(progress, BestillingProgress::setTpsSyncStatus,
                    status.stream()
                            .map(detalj -> String.format("%s:%s", detalj.getMiljoe(),
                                    ErrorStatusDecoder.encodeStatus(detalj.isOk() ? detalj.getStatus() :
                                            StringUtils.trimToEmpty(String.format("FEIL: %s", detalj.getUtfyllendeMelding())))))
                            .collect(Collectors.joining(",")));
            return progress;
        };
    }
}
