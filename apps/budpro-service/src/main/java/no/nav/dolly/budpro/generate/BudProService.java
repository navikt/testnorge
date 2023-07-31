package no.nav.dolly.budpro.generate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.budpro.ansettelsestype.AnsettelsestypeService;
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

    List<BudproRecord> randomize(int seed, int limit) {
        var random = new Random(seed);
        var list = new ArrayList<BudproRecord>(limit);
        for (int i = 0; i < limit; i++) {
            var aga = 1;
            String ansattnavn = null;
            var ansettelsestype = ansettelsestypeService.getRandom(random);
            var aarsloenn = aarsloenn(random);
            String beredskap = null;
            String kode = null;
            String datoFraStilling = null;
            String datoFraStillingUtlaan = null;
            String datoTilStilling = null;
            String datoTilStillingUtlaan = null;
            String felles = null;
            String fellesBeskrivelse = null;
            var foedselsdato = foedselsdato(random);
            var koststed = koststedService.getRandom(random);
            String lederT = null;
            String ledersNavn = null;
            String ledersRessursnummer = null;
            String midlertidigLoennstilskudd = null;
            String oppgave = null;
            String oppgaveBeskrivelse = null;
            String orgenhet = null;
            String orgenhetT = null;
            String permisjon = null;
            String produkt = null;
            String produktBeskrivelse = null;
            String refusjoner = null;
            String ressurs = null;
            String ressursnummer = null;
            String sluttetDato = null;
            String statskonto = null;
            String stillingsnummer = null;
            String stillingsprosent = null;
            String utlaansprosent = null;
            String utlaantTilKoststed = null;
            String utlaantOppgave = null;
            list.add(new BudproRecord(
                    aga,
                    ansattnavn,
                    ansettelsestype,
                    aarsloenn,
                    beredskap,
                    kode,
                    datoFraStilling,
                    datoFraStillingUtlaan,
                    datoTilStilling,
                    datoTilStillingUtlaan,
                    felles,
                    fellesBeskrivelse,
                    foedselsdato,
                    koststed.getId(),
                    koststed.getNavn(),
                    lederT,
                    ledersNavn,
                    ledersRessursnummer,
                    midlertidigLoennstilskudd,
                    oppgave,
                    oppgaveBeskrivelse,
                    orgenhet,
                    orgenhetT,
                    permisjon,
                    produkt,
                    produktBeskrivelse,
                    refusjoner,
                    ressurs,
                    ressursnummer,
                    sluttetDato,
                    statskonto,
                    stillingsnummer,
                    stillingsprosent,
                    utlaansprosent,
                    utlaantTilKoststed,
                    utlaantOppgave
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
