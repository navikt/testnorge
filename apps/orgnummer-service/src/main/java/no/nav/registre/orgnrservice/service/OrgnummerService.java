package no.nav.registre.orgnrservice.service;

import lombok.AllArgsConstructor;
import no.nav.registre.orgnrservice.adapter.OrgnummerAdapter;
import no.nav.registre.orgnrservice.consumer.OrganisasjonConsumer;
import no.nav.registre.orgnrservice.domain.Organisasjon;
import no.nav.testnav.libs.servletcore.util.OrgnummerUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Service
@AllArgsConstructor
public class OrgnummerService {

    private final OrgnummerAdapter orgnummerAdapter;
    private final OrganisasjonConsumer organisasjonApiConsumer;

    public List<String> hentOrgnr(Integer antall) {
        var hentedeOrgnummer = orgnummerAdapter.hentAlleLedige();

        if (hentedeOrgnummer.size() < antall) {
            var manglende = antall - hentedeOrgnummer.size();
            var genererteOrganisasjoner = genererOrgnrsTilDb(manglende, false);

            hentedeOrgnummer.forEach(org -> setLedigForOrgnummer(org.getOrgnummer(), false));
            return Stream.concat(
                            hentedeOrgnummer.stream(),
                            genererteOrganisasjoner.stream())
                    .map(Organisasjon::getOrgnummer)
                    .toList();
        }

        var orgnummer = hentedeOrgnummer
                .subList(0, antall)
                .stream()
                .map(Organisasjon::getOrgnummer)
                .toList();

        orgnummer.forEach(orgnr -> setLedigForOrgnummer(orgnr, false));
        return orgnummer;
    }

    public Organisasjon setLedigForOrgnummer(String orgnummer, boolean ledig) {
        return orgnummerAdapter.save(new Organisasjon(orgnummer, ledig));
    }

    public List<Organisasjon> genererOrgnrsTilDb(Integer antall, boolean ledig) {

        var orgnrs = generateOrgnrs(antall);
        var organisasjoner = orgnrs.stream()
                .map(orgnr -> new Organisasjon(orgnr, ledig))
                .toList();

        return orgnummerAdapter.saveAll(organisasjoner);
    }

    public List<String> generateOrgnrs(Integer antall) {

        List<String> orgnrListe = new ArrayList<>();
        for (int i = 0; i < antall; i++) {
            orgnrListe.add(generateOrgnr());
        }
        return orgnrListe;
    }

    private String generateOrgnr() {

        var generertOrgnummer = OrgnummerUtil.generateOrgnr();
        if (finnesOrgnr(generertOrgnummer)) {
            return generateOrgnr();
        }
        return generertOrgnummer;
    }

    private boolean finnesOrgnr(String orgnummer) {

        return nonNull(orgnummerAdapter.hentByOrgnummer(orgnummer)) ||
                organisasjonApiConsumer.finnesOrgnrIEreg(orgnummer);
    }
}
