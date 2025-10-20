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

import static java.util.Objects.isNull;
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

        return generateNoekkelinfo(foedselsdato, request.getIdenttype())
                .flatMap(noekkelinfo -> personidentifikatorRepository
                        .existsByDatoIdentifikatorAndAllokert(noekkelinfo.datoIdentifikator(), false)
                        .flatMapMany(ledigFinnes -> isTrue(ledigFinnes) ?
                                allokerIdent(noekkelinfo.datoIdentifikator(), foedselsdato) :
                                genererOgAllokerIdent(noekkelinfo, foedselsdato, request.getIdenttype()))
                        .next())
                .map(personidentifikator -> new IdentpoolResponseDTO(
                        personidentifikator.getPersonidentifikator(),
                        personidentifikator.getFoedselsdato(),
                        System.currentTimeMillis() - startTime));
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

    private Flux<Personidentifikator> allokerIdent(String datoIdentifikator, LocalDate foedselsdato) {

        return personidentifikatorRepository.findAvail(datoIdentifikator, false)
                .concatMap(ledig -> {
                    ledig.setFoedselsdato(foedselsdato);
                    ledig.setAllokert(true);
                    ledig.setDatoAllokert(LocalDate.now());
                    return personidentifikatorRepository.save(ledig);
                });
    }

    /**
     * Genererer nøkkelinformasjon for identifikator basert på fødselsdato og identtype.
     * Hvis det allerede finnes allokerte identifikatorer for den genererte datoIdentifikatoren leveres neste invidnummer.
     * Hvis identnummer blir negativt vil metoden rekursivt prøve med en tidligere dato til en ledig identifikator finnes.
     *
     * @param foedselsdato Fødselsdato for personen
     * @param identtype    FNR, DNR eller NPID
     * @return Mono<NoekkelinfoDTO>
     */
    private Mono<NoekkelinfoDTO> generateNoekkelinfo(LocalDate foedselsdato, Identtype identtype) {

        var datoIdentifikator = getDatoIdentifikator(foedselsdato, identtype);

        return personidentifikatorRepository.findAvail(datoIdentifikator)
                .switchIfEmpty(Mono.defer(() -> Mono.just(Personidentifikator.builder()
                        .datoIdentifikator(datoIdentifikator)
                        .individnummer(999)
                        .build())))
                .collectList()
                .flatMap(identer -> {
                    if (identer.getFirst().getIndividnummer() == 999 && isNull(identer.getFirst().getPersonidentifikator())) {
                        return Mono.just(new NoekkelinfoDTO(datoIdentifikator, 999));
                    } else if (identer.stream().anyMatch(ident -> !ident.isAllokert())) {
                        return Mono.just(new NoekkelinfoDTO(datoIdentifikator, identer.getFirst().getIndividnummer()));
                    } else {
                        var individnummer = identer.getFirst().getIndividnummer() - 1;
                        if (individnummer >= 0) {
                            return Mono.just(new NoekkelinfoDTO(datoIdentifikator, individnummer));
                        } else {
                            return generateNoekkelinfo(foedselsdato.minusDays(1), identtype);
                        }
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
