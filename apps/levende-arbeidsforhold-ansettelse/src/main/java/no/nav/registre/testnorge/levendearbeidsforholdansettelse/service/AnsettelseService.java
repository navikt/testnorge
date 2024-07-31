package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.DatoIntervall;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.Arbeidsforhold;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
    private final AnsettelseLoggService ansettelseLoggService;
    private static final int MIN_ALDER = 18;
    private static final int MAKS_ALDER = 70;
    private final String yrke = "7125102";
    private List<DatoIntervall> aldersspennList = new ArrayList<>();

    @EventListener(ApplicationReadyEvent.class)
    public void runAnsettelseService() {
        Thread thread = new Thread(this::ansettelseService);
        thread.start();
        try {
            thread.join(3000000); // Timeout after 30 seconds
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

        initialiserDatoListe();

        Map<String, String> parametere = hentParametere();

        //List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
        List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(dbParametere.get("antallOrg"));
        if (organisasjoner.isEmpty()) {
            return;
        }

        int antallPersPerOrg = 0;
        try {
            //antallPersPerOrg = getAntallAnsettelserHverOrg(Integer.parseInt(parametere.get(ANTALL_PERSONER.value)), Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
            antallPersPerOrg = getAntallAnsettelserHverOrg(dbParametere.get("antallPers"), dbParametere.get("antallOrg"));
        } catch (NumberFormatException e) {
            log.error("Feil format på verdi");
        }

        List<Double> sannsynlighetFordeling =  List.of(434106.0, 1022448.0, 976833.0, 563804.0, 72363.0);
        AliasMethod alias = new AliasMethod(sannsynlighetFordeling);

        int finalAntallPersPerOrg = antallPersPerOrg;
        Long start = System.currentTimeMillis();
        organisasjoner.forEach(
                organisasjon -> {
                    String postnr = konverterPostnr(organisasjon.getPostadresse().getPostnr());

                    List<Integer> aldersspennIndekser = new ArrayList<>();
                    for (int i = 0; i < finalAntallPersPerOrg; i++) {
                        aldersspennIndekser.add(alias.aliasDraw());
                        Collections.sort(aldersspennIndekser);
                    }

                    Iterator<Integer> aldersspennIterator = aldersspennIndekser.iterator();
                    int iteratorElement = aldersspennIterator.next();

                    Map<Integer, List<Ident>> muligePersonerMap = new HashMap<>();
                    aldersspennIndekser.forEach(
                            indeks -> {
                                if (!muligePersonerMap.containsKey(indeks)) {
                                    log.info("i hent personer: {}, {}, {}", aldersspennList.get(indeks).getTom(), aldersspennList.get(indeks).getFrom().toString(), postnr);
                                    muligePersonerMap.put(indeks, hentPersoner( aldersspennList.get(indeks).getTom().toString(), aldersspennList.get(indeks).getFrom().toString(), postnr));
                                }
                            }
                    );

                    int antallPersAnsatt = 0;

                    List<Ident> ansattePersoner = new ArrayList<>(); //liste/oversikt for logging

                    while (antallPersAnsatt < finalAntallPersPerOrg) {
                        try {

                            List<Ident> muligePersoner = muligePersonerMap.get(iteratorElement);

                            int tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                            Ident tilfeldigPerson = muligePersoner.get(tilfeldigIndex);
                            if (harBareTestnorgeTags(tilfeldigPerson)){
                            if(kanAnsettes(tilfeldigPerson) ) {
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
                                        Long dyration = System.currentTimeMillis();
                                        log.info("tid brukt {}", (dyration - start));
                                        if (aldersspennIterator.hasNext()) {
                                            iteratorElement = aldersspennIterator.next();
                                        }
                                    }
                                }catch (WebClientResponseException e){
                                    log.info("organisasjon: {} {}, person {}", organisasjon.getNavn(), organisasjon.getOrgnummer(), tilfeldigPerson.getIdent());
                                    organisasjon = hentOrganisasjoner(1).getFirst();
                                    continue;
                                }
                            }}
                            muligePersoner.remove(tilfeldigIndex);
                        } catch (NullPointerException e) {
                            muligePersonerMap.replace(iteratorElement, hentPersoner(aldersspennList.get(iteratorElement).getFrom().toString(),
                                    aldersspennList.get(iteratorElement).getTom().toString(), postnr));
                        } catch (Exception e) {
                            log.error(e.toString());
                            break;
                        }
                    }
                    for (Ident person: ansattePersoner){
                        ansettelseLoggService.lagreAnsettelse(person, organisasjon);
                    }
                    log.info("Personer ansatt i org {}, {}: {} \n", organisasjon.getNavn(),
                            organisasjon.getOrgnummer(), ansattePersoner);
                    long duration = System.currentTimeMillis();
                    log.info("Tid brukt {}", (duration-start));
                }

        );
        long end = System.currentTimeMillis();
        log.info("Slutt {}", (end-start));
    }

    private Map<String, String> hentParametere() {
        return jobbService.hentParameterMap();
    }

    private List<OrganisasjonDTO> hentOrganisasjoner(int antall) {
        return hentOrganisasjonNummerService.hentAntallOrganisasjoner(antall);
    }

    private List<Ident> hentPersoner(String tidligsteFoedselsdato, String senesteFoedselsdato, String postnr) {
        pdlService.setFrom(tidligsteFoedselsdato);
        pdlService.setTo(senesteFoedselsdato);
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

    private void initialiserDatoListe(){
        aldersspennList.add(DatoIntervall.builder().tom(LocalDate.now().minusYears(24)).from(LocalDate.now().minusYears(18)).build());
        aldersspennList.add(DatoIntervall.builder().tom(LocalDate.now().minusYears(39)).from(LocalDate.now().minusYears(25)).build());
        aldersspennList.add(DatoIntervall.builder().tom(LocalDate.now().minusYears(54)).from(LocalDate.now().minusYears(40)).build());
        aldersspennList.add(DatoIntervall.builder().tom(LocalDate.now().minusYears(66)).from(LocalDate.now().minusYears(55)).build());
        aldersspennList.add(DatoIntervall.builder().tom(LocalDate.now().minusYears(72)).from(LocalDate.now().minusYears(67)).build());
    }
}
