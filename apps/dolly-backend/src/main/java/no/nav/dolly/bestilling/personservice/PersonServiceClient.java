package no.nav.dolly.bestilling.personservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.pdldata.PdlDataConsumer;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceClient {

    private static final String PDL_SYNC_START = "Info: Synkronisering mot PDL startet ...";
    private static final int TIMEOUT = 100;
    private static final int MAX_SEKUNDER = 30;
    private final PersonServiceConsumer personServiceConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final PdlDataConsumer pdlDataConsumer;
    private final PdlPersonConsumer pdlPersonConsumer;

    public Flux<ClientFuture> syncPerson(DollyPerson dollyPerson, BestillingProgress progress) {

        if (!dollyPerson.isOrdre()) {
            transactionHelperService.persister(progress, BestillingProgress::setPdlPersonStatus, PDL_SYNC_START);
        }
        var startTime = System.currentTimeMillis();
        return Flux.from(getIdentWithRelasjoner(dollyPerson)
                .flatMap(ident -> getPersonService(LocalTime.now().plusSeconds(MAX_SEKUNDER), LocalTime.now(),
                        new PersonServiceResponse(), ident))
                .collectList()
                .flatMap(status ->
                        logStatus(status, startTime)
                                .map(status2 -> futurePersist(dollyPerson, progress, status, status2))));
    }

    private Flux<String> getIdentWithRelasjoner(DollyPerson dollyPerson) {

        if (dollyPerson.getMaster() == Testident.Master.PDLF) {

            return Flux.concat(Flux.just(dollyPerson.getIdent()),
                    pdlDataConsumer.getPersoner(List.of(dollyPerson.getIdent()))
                            .map(FullPersonDTO::getRelasjoner)
                            .flatMap(Flux::fromIterable)
                            .filter(relasjonType -> RelasjonType.GAMMEL_IDENTITET != relasjonType.getRelasjonType())
                            .map(FullPersonDTO.RelasjonDTO::getRelatertPerson)
                            .map(PersonDTO::getIdent));
        } else {

            return Flux.concat(Flux.just(dollyPerson.getIdent()),
                    pdlPersonConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
                            .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                            .map(PdlPersonBolk::getData)
                            .map(PdlPersonBolk.Data::getHentPersonBolk)
                            .flatMap(Flux::fromIterable)
                            .filter(personBolk -> nonNull(personBolk.getPerson()))
                            .flatMap(person -> Flux.fromStream(Stream.of(
                                            person.getPerson().getSivilstand().stream()
                                                    .map(PdlPerson.Sivilstand::getRelatertVedSivilstand)
                                                    .filter(Objects::nonNull),
                                            person.getPerson().getForelderBarnRelasjon().stream()
                                                    .map(PdlPerson.ForelderBarnRelasjon::getRelatertPersonsIdent)
                                                    .filter(Objects::nonNull),
                                            person.getPerson().getFullmakt().stream()
                                                    .map(FullmaktDTO::getMotpartsPersonident))
                                    .flatMap(Function.identity()))));
        }
    }

    private ClientFuture futurePersist(DollyPerson dollyPerson, BestillingProgress progress,
                                       List<PersonServiceResponse> status, Map<String, String> status2) {

        return () -> {
            status.stream()
                    .filter(entry -> dollyPerson.getIdent().equals(entry.getIdent()))
                    .forEach(entry -> {
                        progress.setPdlSync(entry.getStatus().is2xxSuccessful() && isTrue(entry.getExists()));
                        if (!dollyPerson.isOrdre()) {
                            transactionHelperService.persister(progress, BestillingProgress::setPdlPersonStatus, status2.get(dollyPerson.getIdent()));
                        }
                    });
            return progress;
        };
    }

    private Mono<Map<String, String>> logStatus(List<PersonServiceResponse> response, long startTime) {

        return Mono.just(response.stream()
                .map(status -> {
                    if (status.getStatus().is2xxSuccessful() && isTrue(status.getExists())) {
                        log.info("Synkronisering mot PersonService (isPerson) for {} tok {} ms.",
                                status.getIdent(), System.currentTimeMillis() - startTime);
                        return Map.of(status.getIdent(), String.format("Synkronisering mot PDL tok %d ms.",
                                System.currentTimeMillis() - startTime));

                    } else if (status.getStatus().is2xxSuccessful() && isNotTrue(status.getExists())) {
                        log.error("Synkronisering mot PersonService (isPerson) for {} gitt opp etter {} ms.",
                                status.getIdent(), System.currentTimeMillis() - startTime);
                        return Map.of(status.getIdent(), String.format("Feil: Synkronisering mot PDL gitt opp etter %d sekunder.",
                                MAX_SEKUNDER));

                    } else {
                        log.error("Feilet å sjekke om person finnes for ident {}, medgått tid {} ms, feil {}.",
                                status.getIdent(), System.currentTimeMillis() - startTime,
                                errorStatusDecoder.getErrorText(status.getStatus(),
                                        status.getFeilmelding()));
                        return Map.of(status.getIdent(), "Feilet å skjekke status, se logg!");
                    }
                })
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private Flux<PersonServiceResponse> getPersonService(LocalTime tidSlutt, LocalTime tidNo, PersonServiceResponse
            response, String ident) {

        if (isTrue(response.getExists()) || tidNo.isAfter(tidSlutt) ||
                nonNull(response.getStatus()) && !response.getStatus().is2xxSuccessful()) {
            return Flux.just(response);

        } else {
            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT))
                    .flatMap(delayed -> personServiceConsumer.isPerson(ident)
                            .flatMapMany(resultat -> getPersonService(tidSlutt, LocalTime.now(), resultat, ident)));
        }
    }
}
