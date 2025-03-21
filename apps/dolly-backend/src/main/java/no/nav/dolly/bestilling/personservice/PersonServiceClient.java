package no.nav.dolly.bestilling.personservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.data.pdlforvalter.v1.FullmaktDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonServiceClient {

    private static final String PDL_SYNC_START = "Info: Synkronisering mot PDL startet ...";
    private static final int TIMEOUT = 500;
    private final PersonServiceConsumer personServiceConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransactionHelperService transactionHelperService;
    private final ObjectMapper objectMapper;
    private final ApplicationConfig applicationConfig;

    public Flux<ClientFuture> syncPerson(DollyPerson dollyPerson, BestillingProgress progress) {

        if (!dollyPerson.isOrdre()) {
            transactionHelperService.persister(progress, BestillingProgress::setPdlPersonStatus, PDL_SYNC_START);
        }
        var startTime = System.currentTimeMillis();

        return Flux.from(getIdentWithRelasjoner(dollyPerson, progress)
                .flatMap(status -> getPersonService(LocalTime.now().plusSeconds(applicationConfig.getClientTimeout()), LocalTime.now(),
                        new PersonServiceResponse(), status))
                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                .onErrorResume(error -> getError(error, dollyPerson))
                .doOnNext(status -> logStatus(status, startTime))
                .collectList()
                .map(status -> futurePersist(dollyPerson, progress, status))
        );
    }

    private Flux<PersonServiceResponse> getError(Throwable throwable, DollyPerson person) {
        var description = WebClientError.describe(throwable);
        return Flux.just(PersonServiceResponse
                .builder()
                .ident(person.getIdent())
                .formattertMelding("Feil= %s".formatted(ErrorStatusDecoder.encodeStatus(description.getMessage())))
                .status(description.getStatus())
                .exists(false)
                .build());
    }

    private Map<String, Set<String>> getHendelseIder(boolean isOrdre, BestillingProgress progress) {

        var json = isOrdre ?
                progress.getPdlOrdreStatus() :
                transactionHelperService.getProgress(progress, BestillingProgress::getPdlOrdreStatus);

        try {
            var tree = objectMapper.readTree(json);

            var hovedperson = Map.of(tree.path("hovedperson").path("ident").asText(),
                    toStream(tree.path("hovedperson").path("ordrer"))
                            .map(entry -> toStream(entry.path("hendelser"))
                                    .map(hendelse -> hendelse.path("hendelseId").asText())
                                    .collect(Collectors.toSet()))
                            .flatMap(Collection::stream)
                            .filter(StringUtil::isNotBlank)
                            .collect(Collectors.toSet()));

            var relasjoner = toStream(tree.path("relasjoner"))
                    .collect(Collectors.toMap(relasjon -> relasjon.path("ident").asText(),
                            relasjon -> toStream(relasjon.path("ordrer"))
                                    .map(entry -> toStream(entry.path("hendelser"))
                                            .map(hendelse -> hendelse.path("hendelseId").asText())
                                            .collect(Collectors.toSet()))
                                    .flatMap(Collection::stream)
                                    .filter(StringUtils::isNotBlank)
                                    .collect(Collectors.toSet())));

            return Stream.of(hovedperson, relasjoner)
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        } catch (JsonProcessingException e) {

            throw new DollyFunctionalException("Feilet å hente hendelseId fra oppretting.", e);
        }
    }

    private Stream<JsonNode> toStream(JsonNode node) {

        return StreamSupport.stream(getIterable(node.elements()).spliterator(), false);
    }

    private Iterable<JsonNode> getIterable(Iterator<JsonNode> iterator) {

        return () -> iterator;
    }

    private Flux<Map.Entry<String, Set<String>>> getIdentWithRelasjoner(DollyPerson dollyPerson, BestillingProgress progress) {

        if (dollyPerson.getMaster() == Testident.Master.PDLF) {

            return Flux.fromIterable(getHendelseIder(dollyPerson.isOrdre(), progress).entrySet());

        } else {

            return Flux.concat(Flux.just(Map.entry(dollyPerson.getIdent(), new HashSet<>())),
                    personServiceConsumer.getPdlPersoner(List.of(dollyPerson.getIdent()))
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
                                                    .map(FullmaktDTO::getMotpartsPersonident),
                                            person.getPerson().getVergemaalEllerFremtidsfullmakt().stream()
                                                    .map(PdlPerson.Vergemaal::getVergeEllerFullmektig)
                                                    .map(PdlPerson.VergeEllerFullmektig::getMotpartsPersonident)
                                                    .filter(Objects::nonNull))
                                    .flatMap(Function.identity())))
                            .map(ident -> Map.entry(ident, new HashSet<>())));
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

    private void logStatus(PersonServiceResponse status, long startTime) {

        if (status.getStatus().is2xxSuccessful() && isTrue(status.getExists())) {
            log.info("Synkronisering mot PersonService (isPerson) for {} tok {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);
            status.setFormattertMelding(String.format("Synkronisering mot PDL tok %d ms.",
                    System.currentTimeMillis() - startTime));

        } else if (status.getStatus().is2xxSuccessful() && isNotTrue(status.getExists())) {
            log.error("Synkronisering mot PersonService (isPerson) for {} gitt opp etter {} ms.",
                    status.getIdent(), System.currentTimeMillis() - startTime);
            status.setFormattertMelding(String.format("Feil: Synkronisering mot PDL gitt opp etter %d sekunder.",
                    applicationConfig.getClientTimeout()));
        } else {
            log.error("Feilet å sjekke om person finnes for ident {}, medgått tid {} ms, {}.",
                    status.getIdent(), System.currentTimeMillis() - startTime,
                    errorStatusDecoder.getErrorText(status.getStatus(),
                            status.getFeilmelding()));
            status.setFormattertMelding("Feil: Synkronisering mot PDL gitt opp etter %d sekunder."
                    .formatted(applicationConfig.getClientTimeout()));
        }
    }

    private Flux<PersonServiceResponse> getPersonService(LocalTime tidSlutt, LocalTime tidNo, PersonServiceResponse
            response, Map.Entry<String, Set<String>> ident) {

        if (isTrue(response.getExists()) || tidNo.isAfter(tidSlutt) ||
                nonNull(response.getStatus()) && !response.getStatus().is2xxSuccessful()) {
            return Flux.just(response);

        } else {
            return Flux.just(1)
                    .delayElements(Duration.ofMillis(TIMEOUT))
                    .flatMap(delayed -> personServiceConsumer.isPerson(ident.getKey(), ident.getValue())
                            .flatMapMany(resultat -> getPersonService(tidSlutt, LocalTime.now(), resultat, ident)));
        }
    }
}
