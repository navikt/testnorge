package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.PdlPersonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.PersonRequestDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util.AlderspennList;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util.AliasMethod;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_ORGANISASJONER;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ANTALL_PERSONER;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.ARBEIDSFORHOLD_TYPE;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.STILLINGSPROSENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnsettelseService {

    private final PdlService pdlService;
    private final TenorService tenorService;
    private final ArbeidsforholdService arbeidsforholdService;
    private final ParameterService parameterService;
    private final KodeverkService kodeverkService;
    private final AnsettelseLoggService ansettelseLoggService;

    @Async
    public void runAnsettelseService() {

        Thread thread = new Thread(this::ansettelseService);
        thread.start();
        try {
            thread.join(3_500_000); //Timeout etter 3000 sekunder
            if (thread.isAlive()) {
                thread.interrupt();
                log.info("Timeout occurred");
            }
        } catch (InterruptedException e) {
            log.info("Timet ut");
        }
    }

    @Transactional
    public void ansettelseService() {

        var startTime = System.currentTimeMillis();

        //Henter parametere fra db
        var parametere = parameterService.hentParametere();
        log.info("Startet oppretting av {} personer i {} organisasjoner", parametere.get("antallPersoner"),
                parametere.get("antallOrganisasjoner"));

        //Henter yrkeskoder for å gi tilfeldige yrker
        var yrkeskoder = kodeverkService.hentKodeverkValues(KodeverkNavn.YRKER.value);
        if (yrkeskoder.isEmpty()) {
            return;
        }

        //Initialiserer liste over alderspenn og liste med tidligste og seneste gyldig dato for ansttelse
        var alderspennList = new AlderspennList();
        var datoIntervaller = alderspennList.getDatointervaller();

        //Initialiserer aliasmetode for å benytte mer realistiske alderspenn i ansettelsene
        var sannsynlighetFordeling = AlderspennList.sannsynlighetFordeling;
        var alias = new AliasMethod(sannsynlighetFordeling);

        //Henter organisasjoner fra Tenor
        var organisasjoner = hentOrganisasjoner(Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
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

        var finalAntallPersPerOrg = antallPersPerOrg;
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
                    var muligePersonerMap = new HashMap<Integer, List<PdlPersonDTO.Person>>();
                    aldersspennIndekser.forEach(
                            indeks -> {
                                if (!muligePersonerMap.containsKey(indeks)) {
                                    muligePersonerMap.put(indeks, hentPersoner(datoIntervaller.get(indeks).getFrom().toString(), datoIntervaller.get(indeks).getTom().toString(), postnr));
                                }
                            });

                    var ansattePersoner = new ArrayList<PdlPersonDTO.Person>();

                    //Ansetter personer
                    while (ansattePersoner.size() < finalAntallPersPerOrg) {
                        try {
                            var muligePersoner = muligePersonerMap.get(iteratorElement);

                            var tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                            var tilfeldigPerson = muligePersoner.get(tilfeldigIndex);
                            var ident = tilfeldigPerson.getFolkeregisteridentifikator().stream()
                                    .filter(PdlPersonDTO.Person.Folkeregisteridentifikator::isIBRUK)
                                    .map(PdlPersonDTO.Person.Folkeregisteridentifikator::getIdentifikasjonsnummer)
                                    .findFirst().orElse(null);

                            var stillingsprosent = Double.parseDouble(parametere.get(STILLINGSPROSENT.value));
                            var arbeidsforholdList = arbeidsforholdService.hentArbeidsforhold(ident);

                            if (kanAnsettes(stillingsprosent, arbeidsforholdList)) {
                                var tilfeldigYrke = hentTilfeldigYrkeskode(yrkeskoder);

                                //Try-catch fordi vi møtte på problemer der noen org ikke fikk suksessfulle ansettelser
                                try {
                                    var ansettSporring = ansettPerson(ident,
                                            organisasjon.getOrganisasjonsnummer(),
                                            tilfeldigYrke, parametere.get(ARBEIDSFORHOLD_TYPE.value),
                                            parametere.get(STILLINGSPROSENT.value));
                                    if (ansettSporring.isPresent() && ansettSporring.get().is2xxSuccessful()) {
                                        ansattePersoner.add(tilfeldigPerson);

                                        if (aldersspennIterator.hasNext()) {
                                            iteratorElement = aldersspennIterator.next();
                                        }
                                    }
                                } catch (WebClientResponseException e) {
                                    log.error(e.toString());
                                    //Løsningen var å hente en ny tilfeldig organisasjon, da ingen personer fikk
                                    //vellykket ansettelse uansett hvor mange vi prøvde å hente
                                    organisasjon = hentOrganisasjoner(1).getFirst();
                                    continue;
                                }
                            }
                            muligePersoner.remove(tilfeldigIndex);

                        } catch (NullPointerException e) {
                            log.error(e.toString(), e);
                            //Henter ny liste med mulige personer dersom den forrige blir tom uten at man fikk ansatt nok
                            muligePersonerMap.replace(iteratorElement, hentPersoner(datoIntervaller.get(iteratorElement).getFrom().toString(),
                                    datoIntervaller.get(iteratorElement).getTom().toString(), postnr));
                        } catch (Exception e) {
                            log.error(e.toString(), e);
                            break;
                        }
                    }
                    //Logging til db
                    for (var person : ansattePersoner) {
                        ansettelseLoggService.lagreAnsettelse(person, organisasjon, Double.parseDouble(parametere.get(STILLINGSPROSENT.value)), parametere.get(ARBEIDSFORHOLD_TYPE.value));
                    }
                    long endTime = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    log.info("Opprettet antall arbeidsforhold: {}, medgått tid: {} sekunder",
                            ansattePersoner.size(), totalTime /1000);
                }
        );
    }

    private List<OrganisasjonDTO> hentOrganisasjoner(int antall) {
        return tenorService.hentOrganisasjoner(antall);
    }

    private List<PdlPersonDTO.Person> hentPersoner(String tidligsteFoedselsdato, String senesteFoedselsdato, String postnr) {

        return pdlService.getPersoner(PersonRequestDTO.builder()
                        .from(tidligsteFoedselsdato)
                        .to(senesteFoedselsdato)
                        .postnr(postnr)
                        .resultsPerPage(10)
                .build());
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

    private Optional<HttpStatusCode> ansettPerson(String ident, String orgnummer, String yrke,
                                                  String arbeidsforholdstype, String stillingsprosent) {

        return arbeidsforholdService.opprettArbeidsforhold(ident, orgnummer, yrke, arbeidsforholdstype, stillingsprosent);
    }

    private int getAntallAnsettelserHverOrg(int antallPers, int antallOrg) {
        //Kan implementere mer tilfeldig fordelig, foreløpig får alle organisasjonene like mange folk
        return antallPers / antallOrg;
    }

    private int tilfeldigTall(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    private String hentTilfeldigYrkeskode(List<String> yrkeskoder) {
        return yrkeskoder.get(tilfeldigTall(yrkeskoder.size()));
    }

    private String konverterPostnr(String postnr) {
        return postnr.charAt(0) + "???";
    }
}
