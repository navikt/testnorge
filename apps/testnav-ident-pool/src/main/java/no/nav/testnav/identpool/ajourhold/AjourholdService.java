package no.nav.testnav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.util.IdentGeneratorUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static no.nav.testnav.identpool.util.IdentGeneratorUtility.genererIdenterMap;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class AjourholdService {

    private static final LocalDate FOEDT_ETTER = LocalDate.of(1900, 1, 1);
    private static final String NAV_SYNTETISKE = "NAV-syntetiske";
    private static final String VANLIGE = "vanlige";

    private static final int MAX_SIZE_TPS_QUEUE = 80;
    private static final int MIN_ANTALL_IDENTER_PER_DAG = 2;
    private static final int MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR = 10;

    private final IdentRepository identRepository;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Flux<String> checkCriticalAndGenerate() {

        var yearsToGenerate = ChronoUnit.YEARS.between(FOEDT_ETTER, LocalDate.now());

        return Flux.range(LocalDate.now().minusYears(yearsToGenerate).getYear(), (int) yearsToGenerate + 1)
                .flatMap(year -> Flux.concat(
                        checkAndGenerateForDate(year, Identtype.FNR, false),
                        checkAndGenerateForDate(year, Identtype.FNR, true),
                        checkAndGenerateForDate(year, Identtype.DNR, false),
                        checkAndGenerateForDate(year, Identtype.DNR, true),
                        checkAndGenerateForDate(year, Identtype.BOST, false),
                        checkAndGenerateForDate(year, Identtype.BOST, true)));
    }

    /**
     * Sjekker om det finnes identer som mangler i ident-pool for et gitt år og type.
     *
     * @param year
     * @param type
     * @param syntetiskIdent
     * @return
     */
    protected Mono<String> checkAndGenerateForDate(
            int year,
            Identtype type,
            boolean syntetiskIdent) {

        var antallPerDag = adjustForYear(year, IdentDistribusjonUtil.antallPersonerPerDagPerAar(year));

        return countNumberOfMissingIdents(year, type, antallPerDag, syntetiskIdent)
                .flatMap(missingIdents ->
                        Mono.just(genererIdenterMap(LocalDate.of(year, 1, 1),
                                        LocalDate.of(year + 1, 1, 1), type, syntetiskIdent))
                                .flatMap(generated -> filterIdents(generated, missingIdents))
                                .flatMapMany(Flux::fromIterable)
                                .map(ident -> IdentGeneratorUtil.createIdent(ident, LEDIG, null))
                                .flatMap(identRepository::save)
                                .count()
                                .doOnNext(antall -> log.info("Ajourhold: år {} {} {} identer har fått allokert antall nye: {}",
                                        year, type, syntetiskIdent ? NAV_SYNTETISKE : VANLIGE, antall))
                                .map(antall -> "Ajourhold: år %d %s %s identer har fått allokert antall nye: %d%n".formatted(
                                        year, type, syntetiskIdent ? NAV_SYNTETISKE : VANLIGE, antall)));
    }

    private Mono<Map<LocalDate, Long>> countNumberOfMissingIdents(
            int year,
            Identtype type,
            int antallPerDag,
            Boolean syntetiskIdent) {

        log.info("Request for å hente antall identer i ident-pool for {} {} {}",
                year, type, isTrue(syntetiskIdent) ? NAV_SYNTETISKE : VANLIGE);

        return identRepository.findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                        LocalDate.of(year, 1, 1),
                        LocalDate.of(year, 12, 31),
                        type,
                        LEDIG,
                        syntetiskIdent)
                .collect(Collectors.groupingBy(Ident::getFoedselsdato, Collectors.counting()))
                .flatMap(avail -> generateDatesForYear(year)
                        .map(date -> Map.entry(date, adjustTotal(avail.get(date), antallPerDag)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .doOnNext(missing -> log.info("Antall identer som mangler i ident-pool for {} {} {}: {}",
                        year, type, isTrue(syntetiskIdent) ? NAV_SYNTETISKE : VANLIGE, missing.values().stream().mapToLong(Long::longValue).sum()));
    }

    private long adjustTotal(Long total, int antallPerDag) {

        if (isNull(total)) {
            return antallPerDag;
        } else if (total < antallPerDag) {
            return antallPerDag - total;
        } else {
            return 0;
        }
    }

    private static Flux<LocalDate> generateDatesForYear(int year) {

        var startDate = LocalDate.of(year, 1, 1);
        var endDate = LocalDate.of(year, 12, 31);

        return Flux.generate(
                () -> startDate,
                (date, sink) -> {
                    sink.next(date);
                    var nextDate = date.plusDays(1);
                    if (nextDate.isAfter(endDate)) {
                        sink.complete();
                    }
                    return nextDate;
                }
        );
    }

    private int adjustForYear(int year, int antallPerDag) {

        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG) {
            antallPerDag = MIN_ANTALL_IDENTER_PER_DAG;
        }

        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR) {
            var dateOfYear = LocalDate.of(year, 1, 1);

            if (dateOfYear.isBefore(LocalDate.now()) && ChronoUnit.YEARS.between(dateOfYear, LocalDate.now()) <= 3) {
                return MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR;
            }
        }
        return antallPerDag;
    }

    private Mono<List<String>> filterIdents(Map<LocalDate, List<String>> genererteIdenter,
                                            Map<LocalDate, Long> missingIdents) {

        return Flux.fromIterable(genererteIdenter.entrySet())
                .flatMap(entry -> Flux.fromIterable(entry.getValue())
                        .concatMap(ident -> identRepository.existsByPersonidentifikator(ident)
                                .flatMap(exist -> isTrue(exist) ? Mono.empty() : Mono.just(ident)))
                        .take(missingIdents.get(entry.getKey())))
                .collectList()
                .doOnNext(antall -> log.info("Antall identer allokert: {} for år {}", antall.size(),
                        genererteIdenter.keySet().stream().map(LocalDate::getYear).findFirst().orElse(null)));
    }

    /**
     * Fjerner FNR/DNR/BNR fra ident-pool-databasen som finnes i prod
     */
    public Mono<List<Ident>> getIdentsAndCheckProd() {

        return identRepository.countAllIkkeSyntetisk(LEDIG, FOEDT_ETTER)
                .doOnNext(count -> log.info("Antall identer som er LEDIG i ident-pool: {}", count))
                .filter(count -> count > 0)
                .flatMap(count -> Flux.range(0, (count / MAX_SIZE_TPS_QUEUE) + 1)
                        .flatMap(i -> identRepository.findAllIkkeSyntetisk(LEDIG, FOEDT_ETTER, PageRequest.of(i, MAX_SIZE_TPS_QUEUE)))
                        .map(Ident::getPersonidentifikator)
                        .collectList()
                        .flatMapMany(idents -> tpsMessagingConsumer.getIdenterProdStatus(new HashSet<>(idents)))
                        .filter(TpsStatusDTO::isInUse)
                        .map(TpsStatusDTO::getIdent)
                        .reduce(new HashSet<String>(), (usedIdents, ident) -> {
                            usedIdents.add(ident);
                            return usedIdents;
                        }))
                .flatMap(identsInUse -> Flux.fromIterable(identsInUse)
                        .flatMap(usedIdent -> identRepository.findByPersonidentifikator(usedIdent)
                                .doOnNext(ident -> {
                                    ident.setRekvireringsstatus(Rekvireringsstatus.I_BRUK);
                                    ident.setRekvirertAv("TPS-PROD");
                                })
                                .flatMap(identRepository::save))
                        .collectList()
                        .doOnNext(usedIdents -> log.info("Fjernet {} identer som er i bruk i prod, " +
                                "men som var markert som LEDIG i ident-pool.", usedIdents.size())));
    }
}
