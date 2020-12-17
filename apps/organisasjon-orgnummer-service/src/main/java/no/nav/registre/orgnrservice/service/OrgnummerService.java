package no.nav.registre.orgnrservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.registre.orgnrservice.adapter.OrganisasjonAdapter;
import no.nav.registre.orgnrservice.consumer.OrganisasjonApiConsumer;
import no.nav.registre.orgnrservice.domain.Organisasjon;

@Service
@AllArgsConstructor
public class OrgnummerService {

    private final OrganisasjonAdapter orgnummerAdapter;
    private final OrganisasjonApiConsumer organisasjonApiConsumer;
    private final GenerateOrgnummerService generateOrgnummerService;

    public List<String> hentOrgnr(Integer antall) {
        var hentedeOrgnummer = orgnummerAdapter.hentAlleLedige();

        if (hentedeOrgnummer.size() < antall) {
            var manglende = antall - hentedeOrgnummer.size();
            var genererteOrganisasjoner = genererOrgnrsTilDb(manglende, false);

            hentedeOrgnummer.forEach( org -> orgnummerAdapter.setIsLedigByOrgnummer(org.getOrgnummer(), false));
            return Stream.concat(
                    hentedeOrgnummer.stream(),
                    genererteOrganisasjoner.stream()
            ).map(Organisasjon::getOrgnummer).collect(Collectors.toList());
        }

        return hentedeOrgnummer
                .subList(0, antall)
                .stream()
                .map( org -> orgnummerAdapter.setIsLedigByOrgnummer(org.getOrgnummer(), false).getOrgnummer())
                .collect(Collectors.toList());
    }

    private List<Organisasjon> setLedigByOrgnummer (List<String> orgnummer, boolean ledig) {
        return orgnummer.stream()
                .map( orgnr -> orgnummerAdapter.setIsLedigByOrgnummer(orgnr, ledig))
                .collect(Collectors.toList());
    }

    public List<Organisasjon> genererOrgnrsTilDb(Integer antall, boolean ledig) {
        List<String> orgnrs = generateOrgnrs(antall);
        var organisasjoner = orgnrs.stream()
                .map(orgnr -> new Organisasjon(orgnr, ledig))
                .collect(Collectors.toList());
        return orgnummerAdapter.saveAll(organisasjoner);
    }

    public List<String> generateOrgnrs(Integer antall) {
        List<String> orgnrListe = new ArrayList<>();
        for (int i = 0; i < antall; i++) {
            orgnrListe.add(generateOrgnr());
        }
        return orgnrListe;
    }

    private String generateOrgnr () {
        var a = generateOrgnummerService.generateOrgnr();
        if (finnesOrgnr(a)){
            generateOrgnr();
        }
        return a;
    }
    public boolean finnesOrgnr(String orgnummer) {
        return organisasjonApiConsumer.getOrgnr(orgnummer) != null;
//        return organisasjonApiConsumer.finnesOrgnrIEreg(orgnummer);
    }
}
