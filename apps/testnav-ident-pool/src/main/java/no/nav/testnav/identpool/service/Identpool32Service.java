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

        var allokert = new AtomicBoolean(true);


        return personidentifikatorRepository.findAvail(datoIdentifikator, false)
                .collectList()
                .flatMap(ledige -> {
                    if (!ledige.isEmpty()) {
                        ledige.getFirst().setFoedselsdato(foedselsdato);
                        ledige.getFirst().setAllokert(true);
                        ledige.getFirst().setDatoAllokert(LocalDate.now());
                        return personidentifikatorRepository.save(ledige.getFirst());
                    } else {
                        return generateNoekkelinfo(foedselsdato, request.getIdenttype())
                                .flatMap(noekkelinfo -> Mono.just(Identpool32GeneratorUtil.generateIdents(
                                                noekkelinfo.datoIdentifikator,
                                                noekkelinfo.individnummer))
                                        .flatMapMany(identer -> Flux.fromIterable(identer)
                                                .map(ident -> {
                                                    var alloker = allokert.getAndSet(false);
                                                    return Personidentifikator.builder()
                                                            .identtype(nonNull(request.getIdenttype()) ? request.getIdenttype() : Identtype.FNR)
                                                            .personidentifikator(ident)
                                                            .datoIdentifikator(noekkelinfo.datoIdentifikator)
                                                            .individnummer(Integer.parseInt(ident.substring(6, 9)))
                                                            .allokert(alloker)
                                                            .foedselsdato(alloker ? foedselsdato : null)
                                                            .datoAllokert(alloker ? LocalDate.now() : null)
                                                            .build();
                                                })
                                                .doOnNext(identifikator ->
                                                        log.info("Lagrer ny ident: {}", identifikator))
                                                .flatMap(personidentifikatorRepository::save))
                                        .filter(Personidentifikator::isAllokert)
                                        .next());
                    }
                })
                .map(Personidentifikator::getPersonidentifikator);
    }



    private Mono<Noekkelinfo> generateNoekkelinfo(LocalDate foedselsdato, Identtype identtype) {

        var datoIdentifikator = getDatoIdentifikator(foedselsdato, identtype);

        return personidentifikatorRepository.existsByDatoIdentifikator(datoIdentifikator)
                .flatMap(exists -> {
                    if (isTrue(exists)) {
                        return personidentifikatorRepository.findAvail(datoIdentifikator, true)
                                .collectList()
                                .map(identer ->
                                        identer.getFirst().getIndividnummer() - 1)
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
