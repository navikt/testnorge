package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.Arbeidsforhold;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_ORGANISASJONER;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_PERSONER;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnsettelseService  {

    private final Map<String, Integer> dbParametere = Map.of("antallPers", 10, "antallOrg", 5);
    private final PdlService pdlService;
    private final HentOrganisasjonNummerService hentOrganisasjonNummerService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final JobbService jobbService;
    private final KodeverkService kodeverkService;
    private static final int MIN_ALDER = 18;
    private static final int MAKS_ALDER = 70;
    private final String yrke = "7125102";

    //@EventListener(ApplicationReadyEvent.class)
    public void runAnsettelseService() {
        Thread thread = new Thread(this::ansettelseService);
        thread.start();
        try {
            thread.join(30000); // Timeout after 30 seconds
            if (thread.isAlive()) {
                thread.interrupt();
                System.out.println("Timeout occurred");
            }
        } catch (InterruptedException e) {
            log.info("Timet ut");
        }
    }

    public void ansettelseService() {
        //TODO: håndtere når personer ikke går opp i antall org
        //TODO: error handling

        List<String> yrkeskoder = hentKodeverk();
        if (yrkeskoder.isEmpty()) {
            return;
        }

        Map<String, String> parametere = hentParametere();
        List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));

        //List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(dbParametere.get("antallOrg"));
        if (organisasjoner.isEmpty()) {
            return;
        }
        int antallPersPerOrg = 0;
        List<Integer> sannsynlighetFordeling =  List.of(434106.0, 1022448.0, 976833, 563804.0, 563804.0, 72363.0)
        AliasMethod alias = new AliasMethod()
        try {
            antallPersPerOrg = getAntallAnsettelserHverOrg(Integer.parseInt(parametere.get(ANTALL_PERSONER.value)), Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
            //antallPersPerOrg = getAntallAnsettelserHverOrg(dbParametere.get("antallPers"), dbParametere.get("antallOrg"));
        } catch (NumberFormatException e) {
            log.error("Feil format på verdi");
        }

        int finalAntallPersPerOrg = antallPersPerOrg;

        //int currentYear = Year.now().getValue();

        String from = LocalDate.now().minusYears(MAKS_ALDER).toString();
        String to = LocalDate.now().minusYears(MIN_ALDER).toString();

        /*
        int from_age = currentYear - MAKS_ALDER;
        int to_age = currentYear - MIN_ALDER;

        String from = "" + from_age;
        String to = "" + to_age;

         */

        organisasjoner.forEach(
                organisasjon -> {
                    int antallPersAnsatt = 0;

                    String postnr = konverterPostnr(organisasjon.getPostadresse().getPostnr());

                    List<Ident> muligePersoner = hentPersoner(from, to, postnr);
                    List<Ident> ansattePersoner = new ArrayList<>(); //liste/oversikt for logging

                    while (antallPersAnsatt < finalAntallPersPerOrg) {
                        try {
                            int tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                            Ident tilfeldigPerson = muligePersoner.get(tilfeldigIndex);

                            if(kanAnsettes(tilfeldigPerson) && harBareTestnorgeTags(tilfeldigPerson)) {
                                log.info("Arbeidsforhold til person {} før ansettelse: {}", tilfeldigPerson.getIdent(),
                                        arbeidsforholdService.hentArbeidsforhold(tilfeldigPerson.getIdent()).toString());

                                String tilfeldigYrke = hentTilfeldigYrkeskode(yrkeskoder);
                                try {
                                    if (ansettPerson(tilfeldigPerson.getIdent(),
                                            organisasjon.getOrgnummer(),
                                            tilfeldigYrke).is2xxSuccessful()) {
                                        log.info("Arbeidsforhold til person {} etter ansettelse: {} \n", tilfeldigPerson.getIdent(),
                                                arbeidsforholdService.hentArbeidsforhold(tilfeldigPerson.getIdent()).toString());
                                        ansattePersoner.add(tilfeldigPerson);
                                        antallPersAnsatt++;
                                    }
                                }catch (WebClientResponseException e){
                                    log.info("organisasjon: {} {}, person {}", organisasjon.getNavn(), organisasjon.getOrgnummer(), tilfeldigPerson.getIdent());
                                    organisasjon = hentOrganisasjoner(1).getFirst();
                                    continue;
                                }
                            }
                            muligePersoner.remove(tilfeldigIndex);
                        } catch (NullPointerException e) {
                            muligePersoner = hentPersoner(from, to, postnr);
                        } catch (Exception e) {
                            log.error(e.toString());
                            break;
                        }
                    }

                    log.info("Personer ansatt i org {}, {}: {} \n", organisasjon.getNavn(),
                            organisasjon.getOrgnummer(), ansattePersoner);
                }
        );
    }

    private Map<String, String> hentParametere() {
        return jobbService.hentParameterMap();
    }

    private List<OrganisasjonDTO> hentOrganisasjoner(int antall) {
        return hentOrganisasjonNummerService.hentAntallOrganisasjoner(antall);
    }

    private List<Ident> hentPersoner(String tidligsteFoedselsaar, String senesteFoedselsaar, String postnr) {
        pdlService.setFrom(tidligsteFoedselsaar);
        pdlService.setTo(senesteFoedselsaar);
        pdlService.setPostnr(postnr);
        return pdlService.getPersoner();
    }

    private boolean kanAnsettes(Ident person) {
        List<Arbeidsforhold> arbeidsforholdList = arbeidsforholdService.hentArbeidsforhold(person.getIdent());
        if (!arbeidsforholdList.isEmpty()) {
            for (Arbeidsforhold arbeidsforhold : arbeidsforholdList) {
                if (arbeidsforhold.getAnsettelsesperiode().getPeriode().getTom() == null) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean harBareTestnorgeTags(Ident person){

        List<String> tags = pdlService.HentTags(person.getIdent());
        return tags.size() == 1 && tags.getFirst().equals("TESTNORGE");

    }

    private HttpStatusCode ansettPerson(String ident, String orgnummer, String yrke) {
        return arbeidsforholdService.opprettArbeidsforhold(ident, orgnummer, yrke);
    }

    private int getAntallAnsettelserHverOrg(int antallPers, int antallOrg) {
        return antallPers/antallOrg;
    }

    private int tilfeldigTall(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    private List<String> hentKodeverk() {
        return kodeverkService.hentKodeverkValues(KodeverkNavn.YRKER.value);
    }

    private String hentTilfeldigYrkeskode(List<String> yrkeskoder) {
        return yrkeskoder.get(tilfeldigTall(yrkeskoder.size()));
    }

    private String konverterPostnr(String postnr) {
        return postnr.charAt(0) + "???";
    }
}
