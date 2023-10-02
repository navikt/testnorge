package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.budpro.ansettelsestype.AnsettelsestypeService;
import no.nav.dolly.budpro.identities.GeneratedNameService;
import no.nav.dolly.budpro.kommune.KommuneService;
import no.nav.dolly.budpro.koststed.KoststedService;
import no.nav.dolly.budpro.ressursnummer.LeaderGenerator;
import no.nav.dolly.budpro.ressursnummer.ResourceNumberGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
class BudProService {

    private final AnsettelsestypeService ansettelsestypeService;
    private final KoststedService koststedService;
    private final GeneratedNameService nameService;
    private final KommuneService kommuneService;


    List<BudproRecord> randomize(Long seed, int numberOfEmployees) {

        var random = seed == null ? new Random() : new Random(seed);

        var numberOfLeaders = (int) Math.ceil((double) numberOfEmployees / 10);
        var allNames = nameService.getNames(seed, numberOfEmployees + numberOfLeaders);
        var employeeNames = Arrays.copyOfRange(allNames, numberOfLeaders, numberOfEmployees + numberOfLeaders);

        var leaderNames = Arrays.copyOfRange(allNames, 0, numberOfLeaders);
        var resourceNumberGenerator = new ResourceNumberGenerator(random);
        var leaderResourceNumbers = resourceNumberGenerator.get(numberOfLeaders);
        var leaderGenerator = new LeaderGenerator(leaderNames, leaderResourceNumbers);

        var list = new ArrayList<BudproRecord>(numberOfEmployees);
        for (int i = 0; i < numberOfEmployees; i++) {

            var leader = leaderGenerator.getRandom(random);
            var kommune = kommuneService.getRandom(random);

            var aga = "060501180000";
            String agaBeskrivelse = "NAV Trygder, pensjon";
            var ansettelsestype = ansettelsestypeService.getRandom(random);
            String arbeidsstedKommune = kommune.getId();
            String felles = "000000";
            String fellesBeskrivelse = "USPESIFISERT";
            String fraDato = null;
            var foedselsdato = foedselsdato(random);
            var koststed = koststedService.getRandom(random);
            String koststedUtlaantFra = null;
            String koststedUtlaantFraBeskrivelse = null;
            String lederUtlaantFra = leader.utlaantFra();
            String ledersNavn = leader.navn();
            String ledersRessursnummer = leader.ressursnummer();
            String navn = employeeNames[i];
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
            String ressursnummer = resourceNumberGenerator.next();
            String sluttetDato = LocalDate.of(2099, 12, 31).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            String skattekommune = kommune.getId();
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
                    skattekommune,
                    sluttetDato,
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
