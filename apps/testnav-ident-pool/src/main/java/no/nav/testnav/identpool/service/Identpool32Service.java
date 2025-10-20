package no.nav.testnav.identpool.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Personidentifikator;
import no.nav.testnav.identpool.dto.IdentpoolResponseDTO;
import no.nav.testnav.identpool.dto.NoekkelinfoDTO;
import no.nav.testnav.identpool.providers.v1.support.RekvirerIdentRequest;
import no.nav.testnav.identpool.repository.PersonidentifikatorRepository;
import no.nav.testnav.identpool.util.Identpool32GeneratorUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class Identpool32Service {

    private static final LocalDate START_DATO = LocalDate.of(1900, 1, 1);
    private static final Random RANDOM = new SecureRandom();

    private final PersonidentifikatorRepository personidentifikatorRepository;

    public Mono<IdentpoolResponseDTO> generateIdent(RekvirerIdentRequest request) {

        var startTime = System.currentTimeMillis();

        var foedselsdato = generateRandomLocalDate(request.getFoedtEtter(), request.getFoedtFoer());

        var datoIdentifikator = getDatoIdentifikator(foedselsdato, request.getIdenttype());

        return personidentifikatorRepository.existsByDatoIdentifikatorAndAllokert(datoIdentifikator, false)
                .flatMapMany(ledigFinnes -> {
                    if (isTrue(ledigFinnes)) {
                        return allokerIdent(datoIdentifikator, foedselsdato);
                    } else {
                        return generateNoekkelinfo(foedselsdato, request.getIdenttype())
                                .flatMapMany(noekkelinfo -> personidentifikatorRepository
                                        .existsByDatoIdentifikatorAndAllokert(noekkelinfo.datoIdentifikator(), false)
                                        .flatMapMany(ledigFinnes2 -> isTrue(ledigFinnes2) ?
                                                allokerIdent(noekkelinfo.datoIdentifikator(), foedselsdato) :
                                                genererOgAllokerIdent(noekkelinfo, foedselsdato, request.getIdenttype())));
                    }
                })
                .next()
                .map(Personidentifikator::getPersonidentifikator)
                .map(ident -> new IdentpoolResponseDTO(ident, foedselsdato, System.currentTimeMillis() - startTime));
    }

    private Flux<Personidentifikator> genererOgAllokerIdent(NoekkelinfoDTO noekkelinfo, LocalDate foedselsdato, Identtype identtype) {

        var allokert = new AtomicBoolean(true);

        return Mono.just(Identpool32GeneratorUtil.generateIdents(noekkelinfo))
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

    /**
     * Genererer nøkkelinformasjon for identifikator basert på fødselsdato og identtype.
     * Hvis det allerede finnes allokerte identifikatorer for den genererte datoIdentifikatoren leveres neste invidnummer.
     * Hvis identnummer blir negativt vil metoden rekursivt prøve med en tidligere dato til en ledig identifikator finnes.
     *
     * @param foedselsdato Fødselsdato for personen
     * @param identtype FNR, DNR eller NPID
     * @return Mono<NoekkelinfoDTO>
     */
    private Mono<NoekkelinfoDTO> generateNoekkelinfo(LocalDate foedselsdato, Identtype identtype) {

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
                                        Mono.just(new NoekkelinfoDTO(datoIdentifikator, individnummer)) :
                                        generateNoekkelinfo(foedselsdato.minusDays(1), identtype));
                    } else {
                        return Mono.just(new NoekkelinfoDTO(datoIdentifikator, 999));
                    }
                });
    }

    private static LocalDate generateRandomLocalDate(LocalDate startDate, LocalDate endDate) {

        long startEpochDay = (nonNull(startDate) ? startDate : START_DATO).toEpochDay();
        long endEpochDay = (nonNull(endDate) ? endDate : LocalDate.now()).toEpochDay();

        long randomEpochDay = RANDOM.nextLong(startEpochDay, endEpochDay + 1); // +1 to include endDate

        return LocalDate.ofEpochDay(randomEpochDay);
    }

    private static String getDatoIdentifikator(LocalDate foedselsdato, Identtype identtype) {

        return String.format("%02d", foedselsdato.getDayOfMonth() + (identtype == Identtype.DNR ? 40 : 0)) +
                String.format("%02d", foedselsdato.getMonthValue() + (identtype == Identtype.NPID ? 60 : 40)) +
                String.format("%02d", foedselsdato.getYear() % 100);
    }
}
