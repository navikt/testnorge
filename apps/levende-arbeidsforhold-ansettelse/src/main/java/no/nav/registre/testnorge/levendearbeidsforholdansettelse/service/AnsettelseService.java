package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnsettelseService {

    private final Map<String, Integer> dbParametere = Map.of("antallPers", 10, "antallOrg", 5);
    private final PdlService pdlService;
    private final HentOrganisasjonNummerService hentOrganisasjonNummerService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final String from = "1955";
    private final String to = "2006";
    private final String yrke = "7125102";

    //TODO: Hente params fra JobbService etc.

    public void ansettelsesService() {
        List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner();

        int antallPersPerOrg = getAntallAnsettelsesHverOrg(dbParametere.get("antallPers"), dbParametere.get("antallOrg"));
        //TODO: håndtere når personer ikke går opp i antall org
        //TODO: legge til timeout og håndtere at den ikke finner nok matches

        organisasjoner.forEach(
             organisasjon -> {
                 int antallPersAnsatt = 0;
                 List<Ident> muligePersoner = hentPersoner(from, to, organisasjon.getPostadresse().getPostnr());
                 while (antallPersAnsatt < antallPersPerOrg) {
                     try {
                         int tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                         Ident tilfeldigPerson = muligePersoner.get(tilfeldigIndex);
                         if(kanAnsettes(tilfeldigPerson)) {
                             ansettPerson(tilfeldigPerson.getIdent(), organisasjon.getOrgnummer(), yrke);
                             antallPersAnsatt++;
                         }
                         muligePersoner.remove(tilfeldigIndex);
                     } catch (Exception e) {
                         log.error(e.toString());
                         break;
                     }
                 }
             }
        );
    }

    public List<OrganisasjonDTO> hentOrganisasjoner() {
        return hentOrganisasjonNummerService.hentAntallOrganisasjoner(dbParametere.get("antallOrg"));
    }

    public List<Ident> hentPersoner(String tidligsteFoedselsaar, String senesteFoedselsaar, String postnr) {
        pdlService.setFrom(tidligsteFoedselsaar);
        pdlService.setTo(senesteFoedselsaar);
        pdlService.setPostnr(postnr);
        return pdlService.getPersoner();
    }

    private boolean kanAnsettes(Ident person) {
        List<Arbeidsforhold> arbeidsforholdList = arbeidsforholdService.hentArbeidsforhold(person.getIdent());
        if (arbeidsforholdList.isEmpty()) {
            return true;
        } else {
            for (Arbeidsforhold arbeidsforhold : arbeidsforholdList) {
                if (arbeidsforhold.getAnsettelsesperiode().getPeriode().getTom() != null) {
                    return false;
                }
            }
            return true;
        }
    }

    private void ansettPerson(String ident, String orgnummer, String yrke) {
        arbeidsforholdService.opprettArbeidsforhold(ident, orgnummer, yrke);
    }

    private int getAntallAnsettelsesHverOrg(int antallPers, int antallOrg) {
        return Math.floorDiv(antallPers, antallOrg);
    }

    private int tilfeldigTall(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

}
