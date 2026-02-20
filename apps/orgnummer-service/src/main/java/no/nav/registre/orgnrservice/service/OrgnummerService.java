package no.nav.registre.orgnrservice.service;

import lombok.AllArgsConstructor;
import no.nav.registre.orgnrservice.adapter.OrgnummerAdapter;
import no.nav.registre.orgnrservice.consumer.OrganisasjonConsumer;
import no.nav.registre.orgnrservice.domain.Organisasjon;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@AllArgsConstructor
public class OrgnummerService {

    private static final String WEIGHTS = "32765432";

    private final OrgnummerAdapter orgnummerAdapter;
    private final OrganisasjonConsumer organisasjonApiConsumer;

    public Mono<List<String>> hentOrgnr(Integer antall) {
        return orgnummerAdapter.hentAlleLedige()
                .flatMap(hentedeOrgnummer -> {
                    if (hentedeOrgnummer.size() < antall) {
                        var manglende = antall - hentedeOrgnummer.size();
                        return genererOrgnrsTilDb(manglende, false)
                                .collectList()
                                .flatMap(genererteOrganisasjoner ->
                                        Flux.fromIterable(hentedeOrgnummer)
                                                .flatMap(org -> setLedigForOrgnummer(org.getOrgnummer(), false))
                                                .then(Mono.just(
                                                        java.util.stream.Stream.concat(
                                                                        hentedeOrgnummer.stream(),
                                                                        genererteOrganisasjoner.stream())
                                                                .map(Organisasjon::getOrgnummer)
                                                                .toList()
                                                ))
                                );
                    }

                    var orgnummer = hentedeOrgnummer
                            .subList(0, antall)
                            .stream()
                            .map(Organisasjon::getOrgnummer)
                            .toList();

                    return Flux.fromIterable(orgnummer)
                            .flatMap(orgnr -> setLedigForOrgnummer(orgnr, false))
                            .then(Mono.just(orgnummer));
                });
    }

    public Mono<Organisasjon> setLedigForOrgnummer(String orgnummer, boolean ledig) {
        return orgnummerAdapter.save(new Organisasjon(orgnummer, ledig));
    }

    public Flux<Organisasjon> genererOrgnrsTilDb(Integer antall, boolean ledig) {
        return generateOrgnrs(antall)
                .map(orgnr -> new Organisasjon(orgnr, ledig))
                .flatMap(orgnummerAdapter::save);
    }

    public Flux<String> generateOrgnrs(Integer antall) {
        return Flux.range(0, antall)
                .flatMap(i -> generateOrgnr());
    }

    private Mono<String> generateOrgnr() {
        return Mono.defer(() -> {
            var generertOrgnummer = generateOrgnrValue();
            return finnesOrgnr(generertOrgnummer)
                    .flatMap(finnes -> finnes ? generateOrgnr() : Mono.just(generertOrgnummer));
        });
    }

    private Mono<Boolean> finnesOrgnr(String orgnummer) {
        return orgnummerAdapter.hentByOrgnummer(orgnummer)
                .map(org -> true)
                .defaultIfEmpty(false)
                .flatMap(existsInDb -> {
                    if (existsInDb) {
                        return Mono.just(true);
                    }
                    return organisasjonApiConsumer.finnesOrgnrIEreg(orgnummer);
                });
    }

    private static String generateOrgnrValue() {
        int random = ThreadLocalRandom.current().nextInt(80000000, 99999999);
        String randomString = String.valueOf(random);
        int controlDigit = calculateControlDigit(randomString);
        if (controlDigit < 0 || controlDigit > 9) {
            return generateOrgnrValue();
        }
        String orgnr = randomString + controlDigit;
        if (orgnr.length() != 9) {
            return generateOrgnrValue();
        }
        return orgnr;
    }

    private static int calculateControlDigit(String randomString) {
        int weightedSum = 0;
        for (int index = 0; index < randomString.length(); index++) {
            int vekt = Character.getNumericValue(WEIGHTS.charAt(index));
            int numericValue = Character.getNumericValue(randomString.charAt(index));
            weightedSum += (vekt * numericValue);
        }
        int rest = weightedSum % 11;
        if (rest == 0) {
            return 0;
        } else if (rest == 1) {
            return -1;
        }
        return 11 - rest;
    }
}
