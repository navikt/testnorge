package no.nav.testnav.identpool.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Personidentifikator;
import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import no.nav.testnav.identpool.util.Identpool32GeneratorUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class Identpool32Service {

    private static final LocalDate START_DATO = LocalDate.of(1900, 1, 1);

    private final PersonidentifikatorRepository personidentifikatorRepository;

    public Mono<String> generateIdent(RekvirerIdentRequest request) {

        var foedselsdato = generateRandomLocalDate(request.getFoedtEtter(), request.getFoedtFoer());

        var datoIdentifikator = getDatoIdentifikator(foedselsdato, request.getIdenttype());

        return personidentifikatorRepository.existsByDatoIdentifikator(datoIdentifikator)
                .flatMapMany(eksisterer -> {
                    if (isTrue(eksisterer)) {
                        return personidentifikatorRepository.existsByDatoIdentifikatorAndAllokert(datoIdentifikator, false)
                                .flatMapMany(ledigFinnes -> {
                                    if (isTrue(ledigFinnes)) {
                                        return allokerIdent(datoIdentifikator, foedselsdato);
                                    } else {
                                        return generateNoekkelinfo(foedselsdato, request.getIdenttype())
                                                .flatMapMany(noekkelinfo -> personidentifikatorRepository
                                                        .existsByDatoIdentifikatorAndAllokert(noekkelinfo.datoIdentifikator, false)
                                                        .flatMapMany(ledigFinnes2 -> isTrue(ledigFinnes2) ?
                                                                allokerIdent(noekkelinfo.getDatoIdentifikator(), foedselsdato) :
                                                                genererOgAllokerIdent(noekkelinfo, foedselsdato, request.getIdenttype())));
                                    }
                                });
                    } else {
                        return genererOgAllokerIdent(datoIdentifikator, 999, foedselsdato, request.getIdenttype());
                    }
                })
                .next()
                .map(Personidentifikator::getPersonidentifikator);
    }

    private Flux<Personidentifikator> genererOgAllokerIdent(Noekkelinfo noekkelinfo, LocalDate foedselsdato, Identtype identtype) {

        return genererOgAllokerIdent(noekkelinfo.datoIdentifikator, noekkelinfo.individnummer, foedselsdato, identtype);
    }

    private Flux<Personidentifikator> genererOgAllokerIdent(String datoindikator, int individnummer, LocalDate foedselsdato, Identtype identtype) {

        var allokert = new AtomicBoolean(true);

        return Mono.just(Identpool32GeneratorUtil.generateIdents(
                        datoindikator,
                        individnummer))
                .flatMapMany(Flux::fromIterable)
                .map(ident -> {
                    var alloker = allokert.getAndSet(false);
                    return Personidentifikator.builder()
                            .identtype(nonNull(ident) ? identtype : Identtype.FNR)
                            .personidentifikator(ident)
                            .datoIdentifikator(ident.substring(0, 6))
                            .individnummer(Integer.parseInt(ident.substring(6, 9)))
                            .allokert(alloker)
                            .foedselsdato(alloker ? foedselsdato : null)
                            .datoAllokert(alloker ? LocalDate.now() : null)
                            .build();
                })
                .flatMap(personidentifikatorRepository::save)
                .filter(Personidentifikator::isAllokert);
    }

    private Mono<Personidentifikator> allokerIdent(String datoIdentifikator, LocalDate foedselsdato) {

        return personidentifikatorRepository.findAvail(datoIdentifikator, false)
                .concatMap(ledig -> {
                    ledig.setFoedselsdato(foedselsdato);
                    ledig.setAllokert(true);
                    ledig.setDatoAllokert(LocalDate.now());
                    return personidentifikatorRepository.save(ledig);
                })
                .next();
    }

    private Mono<Noekkelinfo> generateNoekkelinfo(LocalDate foedselsdato, Identtype identtype) {

        var datoIdentifikator = getDatoIdentifikator(foedselsdato, identtype);

        return personidentifikatorRepository.existsByDatoIdentifikator(datoIdentifikator)
                .flatMap(exists -> {
                    if (isTrue(exists)) {
                        return personidentifikatorRepository.findAvail(datoIdentifikator, true)
                                .collectList()
                                .flatMap(identer -> {
                                    if (identer.getFirst().getIndividnummer() == 0) {
                                        return personidentifikatorRepository
                                                .findAllByDatoIdentifikatorAndIndividnummer(datoIdentifikator, 0)
                                                .collectList()
                                                .map(identer2 ->
                                                        !identer2.isEmpty() && identer2.stream().allMatch(Personidentifikator::isAllokert) ?
                                                                -1 : 0);
                                    } else {
                                        return Mono.just(identer.getFirst().getIndividnummer() - 1);
                                    }
                                })
                                .flatMap(individnummer -> individnummer >= 0 ?
                                        Mono.just(new Noekkelinfo(datoIdentifikator, individnummer)) :
                                        generateNoekkelinfo(foedselsdato.minusDays(1), identtype));
                    } else {
                        return Mono.just(new Noekkelinfo(datoIdentifikator, 999));
                    }
                });
    }

    private static LocalDate generateRandomLocalDate(LocalDate startDate, LocalDate endDate) {

        long startEpochDay = (nonNull(startDate) ? startDate : START_DATO).toEpochDay();
        long endEpochDay = (nonNull(endDate) ? endDate : LocalDate.now()).toEpochDay();

        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1); // +1 to include endDate

        return LocalDate.ofEpochDay(randomEpochDay);
    }

    private static String getDatoIdentifikator(LocalDate foedselsdato, Identtype identtype) {

        return String.format("%02d", foedselsdato.getDayOfMonth() + (identtype == Identtype.DNR ? 40 : 0)) +
                String.format("%02d", foedselsdato.getMonthValue() + (identtype == Identtype.NPID ? 60 : 40)) +
                String.format("%02d", foedselsdato.getYear() % 100);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class Noekkelinfo {

        private String datoIdentifikator;
        private int individnummer;
    }
}
