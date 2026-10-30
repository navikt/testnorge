package no.nav.dolly.bestilling.instdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.instdata.domain.InstdataDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataKdiDTO;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.bestilling.instdata.service.InstKdiHendelseService;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.mapper.MappingContextUtils;
import no.nav.dolly.repository.TransaksjonMappingRepository;
import no.nav.dolly.service.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstdataClient implements ClientRegister {

    private static final String INST2 = "INST2";
    private static final String INST2_STATUS = "INST2_STATUS#%s";
    private static final String KDI_STATUS = "KDI_STATUS#%s";
    private static final String INSTDATA = "Institusjonsopphold";

    private final ErrorStatusDecoder errorStatusDecoder;
    private final InstKdiHendelseService instKdiHendelseService;
    private final InstdataConsumer instdataConsumer;
    private final JsonMapper jsonMapper;
    private final MapperFacade mapperFacade;
    private final TransactionHelperService transactionHelperService;
    private final TransaksjonMappingRepository transaksjonMappingRepository;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if ((bestilling.getInstdata().isEmpty() && isNull(bestilling.getInstdataKdi())) || bestilling.getEnvironments().isEmpty()) {

            return Mono.empty();
        }

        return oppdaterStatus(progress, prepInitStatus(bestilling))
                .then(Flux.merge(doInst2Bestilling(bestilling, dollyPerson, isOpprettEndre)
                                        .map(INST2_STATUS::formatted),
                                doInstKdiBestilling(bestilling, dollyPerson, progress.getBestillingId(), isOpprettEndre)
                                        .map(KDI_STATUS::formatted))
                        .collect(Collectors.joining("|"))
                        .filter(resultat -> !resultat.isBlank())
                        .flatMap(resultat -> oppdaterStatus(progress, resultat)));
    }

    private Mono<String> doInstKdiBestilling(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson,
                                             Long bestillingId, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(_ -> nonNull(bestilling.getInstdataKdi()) &&
                             !bestilling.getEnvironments().isEmpty())
                .flatMap(_ -> instKdiHendelseService.getOppdaterBestilling(bestilling,
                        bestillingId, isOpprettEndre))
                .flatMap(instKdiData -> instdataConsumer.getMiljoer()
                        .flatMapMany(miljoer -> Flux.fromIterable(miljoer.getKdiEnvironments())
                                .filter(miljoe -> bestilling.getEnvironments().contains(miljoe))
                                .map(miljoe -> {
                                    var context = MappingContextUtils.getMappingContext();
                                    context.setProperty("ident", dollyPerson.getIdent());
                                    context.setProperty("miljoe", miljoe);
                                    return mapperFacade.map(instKdiData, InstdataKdiDTO.class, context);
                                }))
                        .flatMap(instKdiRequest -> postInstdataKdi(instKdiRequest, dollyPerson.getIdent()))
                        .collect(Collectors.joining(",")))
                .filter(resultat -> !resultat.isBlank());
    }

    private Flux<String> doInst2Bestilling(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(_ -> !bestilling.getInstdata().isEmpty() && !bestilling.getEnvironments().isEmpty())
                .flatMapMany(_ -> {
                    var context = MappingContextUtils.getMappingContext();
                    context.setProperty("ident", dollyPerson.getIdent());
                    return Mono.just(mapperFacade.mapAsList(bestilling.getInstdata(), InstdataDTO.class, context));
                })
                .flatMap(instdata -> instdataConsumer.getMiljoer()
                        .flatMapMany(miljoer -> Flux.fromIterable(miljoer.getInstitusjonsoppholdEnvironments())
                                .filter(miljoe -> bestilling.getEnvironments().contains(miljoe))
                                .flatMap(miljoe -> postInstdata(isOpprettEndre, instdata, miljoe, bestilling.getId()))
                                .collect(Collectors.joining(","))));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress,
                BestillingProgress::getInstdataStatus,
                BestillingProgress::setInstdataStatus, status);
    }

    @Override
    public void release(List<String> identer) {

        instdataConsumer.deleteInstdata(identer)
                .subscribe(
                        _ -> log.info("Slettet identer fra Inst2"),
                        throwable -> log.error("Feil ved sletting av identer fra Inst2", throwable));
        instdataConsumer.deleteInstKdiData(identer)
                .subscribe(
                        _ -> log.info("Slettet identer fra Institusjonsopphold fengsel (KDI)"),
                        throwable -> log.error("Feil ved sletting av identer fra Institusjonsopphold fengsel (KDI)", throwable));
    }

    private Mono<List<InstdataDTO>> filterInstdata(List<InstdataDTO> instdataRequest, String miljoe, Long bestillingId) {

        if (instdataRequest.isEmpty()) {
            return Mono.just(emptyList());
        }

        return Mono.zip(getOpprettedeOppholdIder(bestillingId, instdataRequest.getFirst().getNorskident(), miljoe),
                        instdataConsumer.getInstdata(instdataRequest.getFirst().getNorskident(), miljoe))
                .map(tuple -> {
                    var opprettedeOppholdIder = tuple.getT1();
                    var eksisterende = tuple.getT2();
                    log.info("Instdata hentet data fra {}: {}", miljoe, eksisterende.getInstitusjonsopphold());
                    return instdataRequest.stream()
                            .filter(request -> eksisterende.getInstitusjonsopphold()
                                    .getOrDefault(miljoe, emptyList())
                                    .stream()
                                    .noneMatch(fraMiljoe -> fraMiljoe.equals(request) ||
                                                            opprettedeOppholdIder.contains(fraMiljoe.getOppholdId())))
                            .toList();
                });
    }

    private Mono<Set<String>> getOpprettedeOppholdIder(Long bestillingId, String ident, String miljoe) {

        return transaksjonMappingRepository.findAllByBestillingId(bestillingId)
                .filter(mapping -> mapping.getSystem().equals(INST2) &&
                                   mapping.getIdent().equals(ident) &&
                                   mapping.getMiljoe().equals(miljoe))
                .next()
                .flatMap(mapping -> {
                    try {
                        return Mono.just(jsonMapper.readValue(mapping.getTransaksjonId(), new TypeReference<Set<String>>() {
                        }));
                    } catch (JacksonException e) {
                        log.error("Transaksjonsmapping for bestillingId {} har ikke gyldig JSON: {}", bestillingId,
                                mapping.getTransaksjonId(), e);
                        return Mono.just(new HashSet<String>());
                    }
                })
                .switchIfEmpty(Mono.just(emptySet()));
    }

    private String prepInitStatus(RsDollyUtvidetBestilling bestilling) {

        var miljoerString = bestilling.getEnvironments().stream()
                .map(miljo -> String.format("%s:%s", miljo, getInfoVenter(INSTDATA)))
                .collect(Collectors.joining(","));
        return Stream.of(bestilling.getInstdata().isEmpty() ? null : INST2_STATUS.formatted(miljoerString),
                        bestilling.getInstdataKdi() == null ? null : KDI_STATUS.formatted(miljoerString))
                .filter(Objects::nonNull)
                .collect(Collectors.joining("|"));
    }

    private String getStatus(InstdataResponse response) {

        return response.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(response.getStatus(),
                        response.getFeilmelding());
    }

    private Mono<String> postInstdata(boolean isNewOpphold, List<InstdataDTO> instdata, String miljoe, Long bestillingId) {

        var oppholdId = new AtomicInteger(0);
        var lagredeOppholdIder = new AtomicReference<Set<String>>(new HashSet<>());

        return (isNewOpphold ? Mono.just(instdata) : filterInstdata(instdata, miljoe, bestillingId))
                .flatMap(newInstdata -> {
                    if (!newInstdata.isEmpty()) {
                        return instdataConsumer.postInstdata(newInstdata, miljoe)
                                .flatMap(response -> {
                                    if (response.getStatus().is2xxSuccessful()) {
                                        lagredeOppholdIder.get().addAll(
                                                response.getInstitusjonsopphold()
                                                        .getOrDefault(miljoe, emptyList()).stream()
                                                        .map(InstdataDTO::getOppholdId)
                                                        .collect(Collectors.toSet()));
                                    }
                                    return Mono.just(response);
                                })
                                .map(response -> String.format("%s:opphold=%d$%s",
                                        miljoe, oppholdId.incrementAndGet(), getStatus(response)))
                                .collect(Collectors.joining(","))
                                .flatMap(status -> saveTransaksjonId(lagredeOppholdIder.get(),
                                        instdata.getFirst().getNorskident(), miljoe, bestillingId)
                                        .zipWith(Mono.just(status)))
                                .map(Tuple2::getT2);
                    } else {
                        return Mono.just(miljoe + ":opphold=1$OK");
                    }
                });
    }

    private Mono<TransaksjonMapping> saveTransaksjonId(Set<String> oppholdIder, String ident, String miljoe, Long bestillingId) {

        return oppholdIder.isEmpty() ?
                Mono.just(new TransaksjonMapping()) :
                transaksjonMappingRepository.findAllByBestillingId(bestillingId)
                        .filter(mapping ->
                                mapping.getSystem().equals(INST2) &&
                                mapping.getIdent().equals(ident) &&
                                mapping.getMiljoe().equals(miljoe))
                        .next()
                        .map(existingMapping -> {
                            existingMapping.setTransaksjonId(jsonMapper.writeValueAsString(oppholdIder));
                            existingMapping.setDatoEndret(now());
                            return existingMapping;
                        })
                        .flatMap(transaksjonMappingRepository::save)
                        .switchIfEmpty(Mono.defer(() -> transaksjonMappingRepository.save(TransaksjonMapping.builder()
                                        .bestillingId(bestillingId)
                                        .system(INST2)
                                        .ident(ident)
                                        .miljoe(miljoe)
                                        .transaksjonId(jsonMapper.writeValueAsString(oppholdIder))
                                        .datoEndret(now())
                                        .build())));
    }

    private Mono<String> postInstdataKdi(InstdataKdiDTO instdata, String ident) {

        return instdataConsumer.postInstdataKdi(instdata, ident)
                .map(response -> "%s:%s".formatted(
                        instdata.getEnvironment(),
                        response.getStatus().is2xxSuccessful() ? "OK" :
                                ErrorStatusDecoder.encodeStatus(errorStatusDecoder.getErrorText(response.getStatus(),
                                        response.getFeilmelding()))));
    }
}
