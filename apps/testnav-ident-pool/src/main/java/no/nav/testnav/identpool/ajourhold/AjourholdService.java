package no.nav.testnav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.ExistsDatoDTO;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.testnav.identpool.service.IdentGeneratorService;
import no.nav.testnav.identpool.service.IdenterAvailService;
import no.nav.testnav.identpool.util.IdentGeneratorUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static no.nav.testnav.identpool.domain.Rekvireringsstatus.LEDIG;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class AjourholdService {

    private static final LocalDate FOEDT_ETTER = LocalDate.of(1870, 1, 1);
    private static final String NAV_SYNTETISKE = "NAV-syntetiske";
    private static final String VANLIGE = "vanlige";

    private static final int MAX_SIZE_TPS_QUEUE = 80;
    private static final int MIN_ANTALL_IDENTER_PER_DAG = 2;
    private static final int MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR = 10;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public Flux<String> checkCriticalAndGenerate() {

        int minYearMinus = 5;

        return Flux.range(LocalDate.now().minusYears(minYearMinus).getYear(), minYearMinus + 1)
                .flatMap(year -> Flux.concat(
//                        checkAndGenerateForDate(year, Identtype.FNR, false),
                        checkAndGenerateForDate(year, Identtype.FNR, true),
//                        checkAndGenerateForDate(year, Identtype.DNR, false),
                        checkAndGenerateForDate(year, Identtype.DNR, true),
//                        checkAndGenerateForDate(year, Identtype.BOST, false),
                        checkAndGenerateForDate(year, Identtype.BOST, true)));
    }

    protected Mono<String> checkAndGenerateForDate(
            int year,
            Identtype type,
            boolean syntetiskIdent) {

        return countNumberOfMissingIdents(year, type, syntetiskIdent)
                .collectList()
                .filter(list -> list.stream().anyMatch(entry -> entry.getAntall() > 0))
                .flatMap(numberOfMissingIdents -> {
                    if (numberOfMissingIdents > 0) {
                        return generateForYear(year, type, numberOfMissingIdents, syntetiskIdent)
                                .flatMapMany(Flux::fromIterable)
                                .map(ident -> IdentGeneratorUtil.createIdent(ident, LEDIG, null))
                                .flatMap(identRepository::save)
                                .count()
                                .map(Integer.class::cast);
                    } else {
                        return Mono.just(numberOfMissingIdents);
                    }
                })
                .doOnNext(antall -> log.info("Ajourhold: år {} {} {} identer har fått allokert antall nye: {}",
                        year, type, syntetiskIdent ? NAV_SYNTETISKE : VANLIGE, antall))
                .map(antall -> "Ajourhold: år %d %s %s identer har fått allokert antall nye: %d%n".formatted(
                        year, type, syntetiskIdent ? NAV_SYNTETISKE : VANLIGE, antall));
    }

    private Flux<ExistsDatoDTO> countNumberOfMissingIdents(
            int year,
            Identtype type,
            Boolean syntetiskIdent) {

        var antallPerDag = adjustForYear(year, IdentDistribusjonUtil.antallPersonerPerDagPerAar(year));

        return identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                        LocalDate.of(year, 1, 1),
                        LocalDate.of(year + 1, 1, 1),
                        type,
                        LEDIG,
                        syntetiskIdent)
                .map(exists -> ExistsDatoDTO.of(Math.max(antallPerDag - exists.getAntall(), 0), exists.getFoedselsdato()));
    }

    protected Mono<List<String>> generateForYear(
            int year,
            Identtype type,
            int numberOfIdents,
            boolean syntetiskIdent) {

        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year + 1) * 2;
        antallPerDag = adjustForYear(year, antallPerDag);

        var firstDate = LocalDate.of(year, 1, 1);
        var lastDate = LocalDate.of(year + 1, 1, 1);
        if (lastDate.isAfter(LocalDate.now())) {
            lastDate = LocalDate.of(year, LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        }
        if (lastDate.isEqual(firstDate)) {
            lastDate = lastDate.plusDays(1);
        }

        var pinMap = identGeneratorService.genererIdenterMap(firstDate, lastDate, type, syntetiskIdent);

        return filterIdents(antallPerDag, pinMap, numberOfIdents, syntetiskIdent);
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

    private Mono<List<String>> filterIdents(int identsPerDay, Map<LocalDate, List<String>> pinMap, int numberOfIdents, boolean syntetiskIdent) {

        return filterAgainstDatabase(identsPerDay, pinMap)
                .collectList()
                .flatMap(identsNotInDatabase -> {
                    if (isTrue(syntetiskIdent)) {
                        return Mono.just(identsNotInDatabase);
                    } else {
                        return tpsMessagingConsumer.getIdenterStatuser(new HashSet<>(identsNotInDatabase))
                                .flatMap(tpsStatus -> {
                                    if (tpsStatus.isInUse()) {
                                        return identRepository.save(IdentGeneratorUtil.createIdent(tpsStatus.getIdent(), Rekvireringsstatus.I_BRUK, "TPS"))
                                                .flatMap(ident -> Mono.empty());
                                    } else {
                                        return Mono.just(tpsStatus.getIdent());
                                    }
                                })
                                .collectList();
                    }
                })
                .flatMapMany(Flux::fromIterable)
                .sort(new RandomComparator<>())
                .collectList()
                .doOnNext(antall -> log.info("Antall identer som er tilgjengelig for allokering: {}", antall.size()))
                .flatMap(ledigeIdenter -> {
                    if (ledigeIdenter.size() > numberOfIdents) {
                        return Mono.just(ledigeIdenter.subList(0, numberOfIdents));
                    } else {
                        return Mono.just(ledigeIdenter);
                    }
                });
//                .flatMapMany(Flux::fromIterable)
//                .map(ident -> IdentGeneratorUtil.createIdent(ident, LEDIG, null))
//                .flatMap(identRepository::save)
//                .collectList()
//                .map(List::size);
    }

    private Flux<String> filterAgainstDatabase(int antallPerDag, Map<LocalDate, List<String>> pinMap) {

        return Flux.fromIterable(pinMap.entrySet())
                .flatMap(entry -> Flux.range(0, Math.min(antallPerDag, entry.getValue().size()))
                        .flatMap(i -> identRepository.existsByPersonidentifikator(entry.getValue().get(i))
                                .flatMap(exists -> isTrue(exists) ? Mono.empty() : Mono.just(entry.getValue().get(i)))));
    }

    /**
     * Fjerner FNR/DNR/BNR fra ident-pool-databasen som finnes i prod
     */
    public Mono<List<Ident>> getIdentsAndCheckProd() {

        return identRepository.countAllIkkeSyntetisk(LEDIG, FOEDT_ETTER)
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

    private static class RandomComparator<T> implements Comparator<T> {

        private final Map<T, Integer> map = new IdentityHashMap<>();
        private final Random random;

        public RandomComparator() {
            this(new SecureRandom());
        }

        public RandomComparator(Random random) {
            this.random = random;
        }

        @Override
        public int compare(T t1, T t2) {
            return Integer.compare(valueFor(t1), valueFor(t2));
        }

        private int valueFor(T t) {
            synchronized (map) {
                return map.computeIfAbsent(t, ignore -> random.nextInt());
            }
        }
    }
}
