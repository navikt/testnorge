package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.DatoIntervall;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util.AlderspennList;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util.AliasMethod;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_ORGANISASJONER;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_PERSONER;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ARBEIDSFORHOLD_TYPE;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.STILLINGSPROSENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnsettelseService  {

    private final PdlService pdlService;
    private final TenorService tenorService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final JobbService jobbService;
    private final KodeverkService kodeverkService;
    private final AnsettelseLoggService ansettelseLoggService;

    public void runAnsettelseService() {
        Thread thread = new Thread(this::ansettelseService);
        thread.start();
        try {
            thread.join(3000000); //Timeout etter 3000 sekunder
            if (thread.isAlive()) {
                thread.interrupt();
                System.out.println("Timeout occurred");
            }
        } catch (InterruptedException e) {
            log.info("Timet ut");
        }
    }

    public void ansettelseService() {
        //Henter yrkeskoder for å gi tilfeldige yrker
        List<String> yrkeskoder = hentKodeverk();
        if (yrkeskoder.isEmpty()) {
            return;
        }

        //Initialiserer liste over alderspenn og liste med tidligste og seneste gyldig dato for ansttelse
        AlderspennList alderspennList = new AlderspennList();
        List<DatoIntervall> datoIntervaller = alderspennList.getDatoListe();

        //Initialiserer aliasmetode for å benytte mer realistiske alderspenn i ansettelsene
        List<Double> sannsynlighetFordeling =  alderspennList.sannsynlighetFordeling;
        AliasMethod alias = new AliasMethod(sannsynlighetFordeling);

        //Henter parametere fra db
        Map<String, String> parametere = hentParametere();

        //Henter organisasjoner fra Tenor
        List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
        if (organisasjoner.isEmpty()) {
            return;
        }

        //Regner ut hvor mange som skal ansettes per org
        int antallPersPerOrg;
        try {
            antallPersPerOrg = getAntallAnsettelserHverOrg(Integer.parseInt(parametere.get(ANTALL_PERSONER.value)), Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
        } catch (NumberFormatException e) {
            log.error("Feil format på verdiene fra db");
            return;
        }

        int finalAntallPersPerOrg = antallPersPerOrg;
        //Kjører ansettelse per org
        organisasjoner.forEach(
            organisasjon -> {
                if (tenorService.hentOrgPostnummer(organisasjon.getOrganisasjonsnummer()) == null) {
                    organisasjon = hentOrganisasjoner(1).getFirst();
                }
                String postnr = konverterPostnr(tenorService.hentOrgPostnummer(organisasjon.getOrganisasjonsnummer()));

                //Trekker alderspenn fra alias for hver pers som skal ansettes
                List<Integer> aldersspennIndekser = new ArrayList<>();
                for (int i = 0; i < finalAntallPersPerOrg; i++) {
                    aldersspennIndekser.add(alias.aliasDraw());
                }

                Iterator<Integer> aldersspennIterator = aldersspennIndekser.iterator();
                int iteratorElement = aldersspennIterator.next();

                //Henter mulige personer per alderspenn basert på postnummer fra org
                Map<Integer, List<Ident>> muligePersonerMap = new HashMap<>();
                aldersspennIndekser.forEach(
                        indeks -> {
                            if (!muligePersonerMap.containsKey(indeks)) {
                                muligePersonerMap.put(indeks, hentPersoner(datoIntervaller.get(indeks).getFrom().toString(), datoIntervaller.get(indeks).getTom().toString(), postnr));
                            }
                        });

                List<Ident> ansattePersoner = new ArrayList<>();

                //Ansetter personer
                while (ansattePersoner.size() < finalAntallPersPerOrg) {
                    try {
                        List<Ident> muligePersoner = muligePersonerMap.get(iteratorElement);

                        var tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                        var tilfeldigPerson = muligePersoner.get(tilfeldigIndex);

                        var stillingsprosent = Double.parseDouble(parametere.get(STILLINGSPROSENT.value));
                        var arbeidsforholdList = arbeidsforholdService.hentArbeidsforhold(tilfeldigPerson.getIdent());

                        if(kanAnsettes(stillingsprosent, arbeidsforholdList) ) {
                            var tilfeldigYrke = hentTilfeldigYrkeskode(yrkeskoder);

                            //Try-catch fordi vi møtte på problemer der noen org ikke fikk suksessfulle ansettelser
                            try {
                                var ansettSporring = ansettPerson(tilfeldigPerson.getIdent(),
                                        organisasjon.getOrganisasjonsnummer(),
                                        tilfeldigYrke,
                                        parametere.get(JobbParameterNavn.STILLINGSPROSENT.value));
                                if (ansettSporring.isPresent() && ansettSporring.get().is2xxSuccessful()) {
                                    ansattePersoner.add(tilfeldigPerson);

                                    if (aldersspennIterator.hasNext()) {
                                        iteratorElement = aldersspennIterator.next();
                                    }
                                }
                            }catch (WebClientResponseException e){
                                log.error(e.toString());
                                //Løsningen var å hente en ny tilfeldig organisasjon, da ingen personer fikk
                                //vellykket ansettelse uansett hvor mange vi prøvde å hente
                                organisasjon = hentOrganisasjoner(1).getFirst();
                                continue;
                            }
                        }
                        muligePersoner.remove(tilfeldigIndex);

                    } catch (NullPointerException e) {
                        log.error(e.toString());
                        //Henter ny liste med mulige personer dersom den forrige blir tom uten at man fikk ansatt nok
                        muligePersonerMap.replace(iteratorElement, hentPersoner(datoIntervaller.get(iteratorElement).getFrom().toString(),
                                datoIntervaller.get(iteratorElement).getTom().toString(), postnr));
                    } catch (Exception e) {
                        log.error(e.toString());
                        break;
                    }
                }
                //Logging til db
                for (Ident person: ansattePersoner){
                    ansettelseLoggService.lagreAnsettelse(person, organisasjon, Double.parseDouble(parametere.get(STILLINGSPROSENT.value)), parametere.get(ARBEIDSFORHOLD_TYPE.value));
                }
            }
        );
    }

    private Map<String, String> hentParametere() {
        return jobbService.hentParameterMap();
    }

    private List<OrganisasjonDTO> hentOrganisasjoner(int antall) {
        return tenorService.hentOrganisasjoner(antall);
    }

    private List<Ident> hentPersoner(String tidligsteFoedselsdato, String senesteFoedselsdato, String postnr) {
        pdlService.setFrom(tidligsteFoedselsdato);
        pdlService.setTo(senesteFoedselsdato);
        pdlService.setPostnr(postnr);
        return pdlService.getPersoner();
    }

    private boolean kanAnsettes(Double stillingsprosent, List<Arbeidsforhold> arbeidsforholdList) {

        if (!arbeidsforholdList.isEmpty()) {
            for (var arbeidsforhold : arbeidsforholdList) {
                for (var arbeidsavtale : arbeidsforhold.getArbeidsavtaler()) {
                    if (arbeidsavtale.getBruksperiode().getTom() == null) {
                        stillingsprosent += arbeidsavtale.getStillingsprosent();
                        if (stillingsprosent > 100) return false;
                    }
                }
            }
        }
        return true;
    }

    private Optional<HttpStatusCode> ansettPerson(String ident, String orgnummer, String yrke, String stillingsprosent) {
        return arbeidsforholdService.opprettArbeidsforhold(ident, orgnummer, yrke, stillingsprosent);
    }

    private int getAntallAnsettelserHverOrg(int antallPers, int antallOrg) {
        //Kan implementere mer tilfeldig fordelig, foreløpig får alle organisasjonene like mange folk
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
