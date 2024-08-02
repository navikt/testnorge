package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.DatoIntervall;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.TagsDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.Arbeidsavtale;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto.OrganisasjonDTO;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold.Arbeidsforhold;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.service.util.AlderspennList;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterNavn.*;

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

    //@EventListener(ApplicationReadyEvent.class)
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
        AlderspennList alderspennList = new AlderspennList();
        List<Double> sannsynlighetFordeling =  alderspennList.sannsynlighetFordeling;
        List<DatoIntervall> datoIntervalls = alderspennList.getDatoListe();

        Map<String, String> parametere = hentParametere();

        List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
        //List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(dbParametere.get("antallOrg"));
        if (organisasjoner.isEmpty()) {
            return;
        }

        int antallPersPerOrg = 0;
        try {
            antallPersPerOrg = getAntallAnsettelserHverOrg(Integer.parseInt(parametere.get(ANTALL_PERSONER.value)), Integer.parseInt(parametere.get(ANTALL_ORGANISASJONER.value)));
            //antallPersPerOrg = getAntallAnsettelserHverOrg(dbParametere.get("antallPers"), dbParametere.get("antallOrg"));
        } catch (NumberFormatException e) {
            log.error("Feil format på verdi");
        }

        AliasMethod alias = new AliasMethod(sannsynlighetFordeling);

        int finalAntallPersPerOrg = antallPersPerOrg;
        Long start = System.currentTimeMillis();
        organisasjoner.forEach(
                organisasjon -> {
                    String postnr = konverterPostnr(tenorService.hentOrgPostnummer(organisasjon.getOrganisasjonsnummer()));

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
                                    muligePersonerMap.put(indeks, hentPersoner(datoIntervalls.get(indeks).getTom().toString(), datoIntervalls.get(indeks).getFrom().toString(), postnr));
                                }
                            });

                    int antallPersAnsatt = 0;

                    List<Ident> ansattePersoner = new ArrayList<>(); //liste/oversikt for logging

                    while (antallPersAnsatt < finalAntallPersPerOrg) {
                        try {

                            List<Ident> muligePersoner = muligePersonerMap.get(iteratorElement);

                            int tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                            Ident tilfeldigPerson = muligePersoner.get(tilfeldigIndex);
                            if(kanAnsettes(tilfeldigPerson) ) {
                                log.info("Arbeidsforhold til person {} før ansettelse: {}", tilfeldigPerson.getIdent(),
                                        arbeidsforholdService.hentArbeidsforhold(tilfeldigPerson.getIdent()).toString());

                                String tilfeldigYrke = hentTilfeldigYrkeskode(yrkeskoder);
                                try {
                                    if (ansettPerson(tilfeldigPerson.getIdent(),
                                            organisasjon.getOrganisasjonsnummer(),
                                            tilfeldigYrke,
                                            parametere.get(JobbParameterNavn.STILLINGSPROSENT.value)).is2xxSuccessful()) {
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
                                    log.info("organisasjon: {} {}, person {}", organisasjon.getNavn(), organisasjon.getOrganisasjonsnummer() , tilfeldigPerson.getIdent());
                                    organisasjon = hentOrganisasjoner(1).getFirst();
                                    continue;
                                }
                            }
                            muligePersoner.remove(tilfeldigIndex);
                        } catch (NullPointerException e) {
                            muligePersonerMap.replace(iteratorElement, hentPersoner(datoIntervalls.get(iteratorElement).getFrom().toString(),
                                    datoIntervalls.get(iteratorElement).getTom().toString(), postnr));
                        } catch (Exception e) {
                            log.error(e.toString());
                            break;
                        }
                    }
                    for (Ident person: ansattePersoner){
                        ansettelseLoggService.lagreAnsettelse(person, organisasjon, Double.parseDouble(parametere.get(STILLINGSPROSENT.value)), parametere.get(ARBEIDSFORHOLD_TYPE.value));
                    }
                    log.info("Personer ansatt i org {}, {}: {} \n", organisasjon.getNavn(),
                            organisasjon.getOrganisasjonsnummer(), ansattePersoner);
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
        return tenorService.hentOrganisasjoner(antall);
    }

    private List<Ident> hentPersoner(String tidligsteFoedselsdato, String senesteFoedselsdato, String postnr) {
        pdlService.setFrom(tidligsteFoedselsdato);
        pdlService.setTo(senesteFoedselsdato);
        pdlService.setPostnr(postnr);
        return pdlService.getPersoner();
    }

    private boolean kanAnsettes(Ident person) {
        Double stillingsprosent = Double.parseDouble(jobbService.hentJobbParameter(JobbParameterNavn.STILLINGSPROSENT.value).getVerdi());
        List<Arbeidsforhold> arbeidsforholdList = arbeidsforholdService.hentArbeidsforhold(person.getIdent());
        if (!arbeidsforholdList.isEmpty()) {
            for (Arbeidsforhold arbeidsforhold : arbeidsforholdList) {
                for (Arbeidsavtale arbeidsavtale : arbeidsforhold.getArbeidsavtaler()) {
                    if (arbeidsavtale.getBruksperiode().getTom() == null) {
                        stillingsprosent += arbeidsavtale.getStillingsprosent();
                        log.info("stillingsprosent: x og tom: null: {}, stillingsprosent: {}", arbeidsavtale.getStillingsprosent(), stillingsprosent);
                        if (stillingsprosent > 100) return false;
                    }
                }
            }
        }
        return true;
    }
/*
    private List<Ident> harBareTestnorgeTags(List<Ident> person){
        List<String> ident = new ArrayList<>();
        person.forEach(pers -> ident.add(pers.getIdent()));
        TagsDTO dto = pdlService.HentTags(ident);

        for(var id : dto.getPersonerTags().entrySet()){
            List<String> value = id.getValue();
            log.info("Tags: {}, bool: {}", value.toString(), (value.size() == 1 && value.getFirst().contains("TESTNORGE")));
            if(!(value.size() == 1 && value.getFirst().contains("TESTNORGE"))){
                String iden = id.getKey();;
                person.removeIf(ide -> ide.getIdent().equals(iden));
            }
        }
        return person;
    }

 */

    private HttpStatusCode ansettPerson(String ident, String orgnummer, String yrke, String stillingsprosent) {
        return arbeidsforholdService.opprettArbeidsforhold(ident, orgnummer, yrke, stillingsprosent);
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
