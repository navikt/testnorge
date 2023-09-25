package no.nav.dolly.bestilling.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.tpsmessagingservice.TpsMessagingConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
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
import java.util.Objects;
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
    private final PdlPersonConsumer pdlPersonConsumer;

    public Flux<ClientFuture> syncPerson(DollyPerson dollyPerson, RsDollyUtvidetBestilling bestilling, BestillingProgress progress) {

        long startTime = System.currentTimeMillis();

        return Flux.fromIterable(bestilling.getEnvironments())
                .filter(PENSJON_MILJOER::contains)
                .collectList()
                .filter(penMiljoer -> !penMiljoer.isEmpty())
                .filter(penMiljoer -> isRelevantBestilling(bestilling) &&
                        (nonNull(bestilling.getPensjonforvalter().getInntekt()) ||
                                !isTransaksjonMapping(dollyPerson.getIdent(), bestilling, penMiljoer)))
                .flatMapMany(penMiljoer ->
                        getRelasjoner(dollyPerson.getIdent())
                                .flatMap(relasjon -> Flux.from(getTpsPerson(startTime, dollyPerson.getIdent(),
                                        penMiljoer, Collections.emptyList(), progress)
                                        .map(status -> prepareResult(relasjon, status, bestilling.getEnvironments(), startTime)))))
                .collectList()
                .flatMapIterable(list -> list)
                .map(status -> futurePersist(progress, dollyPerson.getIdent(), status));
    }

    private Flux<String> getRelasjoner(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData)
                .map(PdlPersonBolk.Data::getHentPersonBolk)
                .flatMap(Flux::fromIterable)
                .filter(personBolk -> nonNull(personBolk.getPerson()))
                .flatMap(person -> Flux.fromStream(Stream.of(
                                Stream.of(ident),
                                person.getPerson().getSivilstand().stream()
                                        .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                        .filter(Objects::nonNull),
                                person.getPerson().getForelderBarnRelasjon().stream()
                                        .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                        .filter(Objects::nonNull))
                        .flatMap(Function.identity())))
                .distinct();
    }

    private boolean isRelevantBestilling(RsDollyUtvidetBestilling bestilling) {

        return nonNull(bestilling.getPensjonforvalter()) &&
                (nonNull(bestilling.getPensjonforvalter().getInntekt()) ||
                        nonNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                        nonNull(bestilling.getPensjonforvalter().getUforetrygd()));
    }

    private boolean isTransaksjonMapping(String ident, RsDollyUtvidetBestilling bestilling, List<String> miljoer) {

        var transaksjoner = transaksjonMappingService.getTransaksjonMapping(ident);

        return (isNull(bestilling.getPensjonforvalter().getAlderspensjon()) ||
                miljoer.stream()
                        .allMatch(miljoe -> transaksjoner.stream()
                                .anyMatch(transaksjon -> miljoe.equals(transaksjon.getMiljoe()) &&
                                        SystemTyper.PEN_AP.name().equals(transaksjon.getSystem())))) &&

                (isNull(bestilling.getPensjonforvalter().getUforetrygd()) ||
                        miljoer.stream()
                                .allMatch(miljoe -> transaksjoner.stream()
                                        .anyMatch(transaksjon -> miljoe.equals(transaksjon.getMiljoe()) &&
                                                SystemTyper.PEN_UT.name().equals(transaksjon.getSystem()))));
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
                                        .ident(ident)
                                        .miljoe(miljoe)
                                        .status("NOK")
                                        .utfyllendeMelding(String.format("Feil: Synkronisering mot TPS gitt opp etter %d sekunder.", MAX_MILLIES / 1000))
                                        .build()))
                .flatMap(Function.identity())
                .toList();
    }

    private ClientFuture futurePersist(BestillingProgress progress, String ident, List<PersonMiljoeDTO> status) {

        return () -> {

            progress.setIsTpsSyncEnv(status.stream()
                    .filter(status1 -> ident.equals(status1.getIdent()))
                    .filter(PersonMiljoeDTO::isOk)
                    .map(PersonMiljoeDTO::getMiljoe)
                    .toList());

            transactionHelperService.persister(progress, BestillingProgress::setTpsSyncStatus,
                    status.stream()
                            .filter(detalj -> ident.equals(detalj.getIdent()))
                            .map(detalj -> String.format("%s:%s", detalj.getMiljoe(),
                                    ErrorStatusDecoder.encodeStatus(detalj.isOk() ? detalj.getStatus() :
                                            StringUtils.trimToEmpty(String.format("FEIL: %s", detalj.getUtfyllendeMelding())))))
                            .collect(Collectors.joining(",")));
            return progress;
        };
    }
}
