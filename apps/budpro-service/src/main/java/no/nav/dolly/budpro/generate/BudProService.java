package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.budpro.ansettelsestype.AnsettelsestypeService;
import no.nav.dolly.budpro.identities.GeneratedNameService;
import no.nav.dolly.budpro.koststed.KoststedService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
class BudProService {

    private final AnsettelsestypeService ansettelsestypeService;
    private final KoststedService koststedService;
    private final GeneratedNameService generatedNameService;

    List<BudproRecord> randomize(Long seed, int limit) {
        var employeeNames = generatedNameService.getNames(seed, limit);
        var random = seed == null ? new Random() : new Random(seed);
        var list = new ArrayList<BudproRecord>(limit);
        for (int i = 0; i < limit; i++) {
            var aga = 1;
            String agaBeskrivelse = null;
            var ansettelsestype = ansettelsestypeService.getRandom(random);
            String arbeidsstedKommune = null;
            String felles = null;
            String fellesBeskrivelse = null;
            String fraDato = null;
            var foedselsdato = foedselsdato(random);
            var koststed = koststedService.getRandom(random);
            String koststedUtlaantFra = null;
            String koststedUtlaantFraBeskrivelse = null;
            String lederUtlaantFra = null;
            String ledersNavn = null;
            String ledersRessursnummer = null;
            String navn = employeeNames[i].toString();
            String oppgave = null;
            String oppgaveBeskrivelse = null;
            String oppgaveUtlaantFra = null;
            String oppgaveUtlaantFraBeskrivelse = null;
            String orgenhet = null;
            String orgenhetNavn = null;
            String permisjonskode = null;
            String produkt = null;
            String produktBeskrivelse = null;
            String produktUtlaantFra = null;
            String produktUtlaantFraBeskrivelse = null;
            String ressursnummer = null;
            String sluttetDato = null;
            String skattekommune = null;
            String statskonto = "060501000000";
            String statskontoKapittel = "0605";
            String statskontoPost = "01";
            String stillingsnummer = null;
            String stillingsprosent = null;
            String tilDato = null;
            String aarsloennInklFasteTillegg = aarsloenn(random);
            list.add(new BudproRecord(
                    aga,
                    agaBeskrivelse,
                    ansettelsestype,
                    arbeidsstedKommune,
                    felles,
                    fellesBeskrivelse,
                    fraDato,
                    foedselsdato,
                    koststed.getId(),
                    koststed.getBeskrivelse(),
                    koststedUtlaantFra,
                    koststedUtlaantFraBeskrivelse,
                    lederUtlaantFra,
                    ledersNavn,
                    ledersRessursnummer,
                    navn,
                    oppgave,
                    oppgaveBeskrivelse,
                    oppgaveUtlaantFra,
                    oppgaveUtlaantFraBeskrivelse,
                    orgenhet,
                    orgenhetNavn,
                    permisjonskode,
                    produkt,
                    produktBeskrivelse,
                    produktUtlaantFra,
                    produktUtlaantFraBeskrivelse,
                    ressursnummer,
                    sluttetDato,
                    skattekommune,
                    statskonto,
                    statskontoKapittel,
                    statskontoPost,
                    stillingsnummer,
                    stillingsprosent,
                    tilDato,
                    aarsloennInklFasteTillegg
            ));
        }
        return list;
    }

    private String aarsloenn(Random random) {
        var value = random.nextInt(500000, 1000000);
        return String.valueOf(value);
    }

    private String foedselsdato(Random random) {
        var ageInYears = random.nextInt(18, 67);
        var then = LocalDate
                .now()
                .minusDays(random.nextInt(365))
                .plusDays(random.nextInt(365))
                .minusYears(ageInYears);
        return then.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

}
