package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.Ident;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    private final String from = "1955";
    private final String to = "2006";
    private final String yrke = "7125102";


    @EventListener(ApplicationReadyEvent.class)
    public void runAnsettelseService() {
        Thread thread = new Thread(this::ansettelseService);
        thread.start();
        try {
            thread.join(30000); // Timeout after 3 seconds
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

        //Map<String, String> parametere = hentParametere();
        //List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(Integer.parseInt(parametere.get("antallOrganisasjoner")));

        List<OrganisasjonDTO> organisasjoner = hentOrganisasjoner(dbParametere.get("antallOrg"));
        if (organisasjoner.isEmpty()) {
            return;
        }
        int antallPersPerOrg = 0;

        try {
            //antallPersPerOrg = getAntallAnsettelserHverOrg(Integer.parseInt(parametere.get("antallPersoner")), Integer.parseInt(parametere.get("antallOrganisasjoner")));
            antallPersPerOrg = getAntallAnsettelserHverOrg(dbParametere.get("antallPers"), dbParametere.get("antallOrg"));
        } catch (NumberFormatException e) {
            log.error("Feil format på verdi");
        }

        int finalAntallPersPerOrg = antallPersPerOrg;

        organisasjoner.forEach(
                organisasjon -> {
                    int antallPersAnsatt = 0;

                    List<Ident> muligePersoner = hentPersoner(from, to, organisasjon.getPostadresse().getPostnr());
                    List<Ident> ansattePersoner = new ArrayList<>(); //liste/oversikt for logging

                    while (antallPersAnsatt < finalAntallPersPerOrg) {
                        try {
                            int tilfeldigIndex = tilfeldigTall(muligePersoner.size());
                            Ident tilfeldigPerson = muligePersoner.get(tilfeldigIndex);

                            if(kanAnsettes(tilfeldigPerson)) {
                                log.info("Arebidsforhold til person {} før ansettelse: {}", tilfeldigPerson.getIdent(),
                                        arbeidsforholdService.hentArbeidsforhold(tilfeldigPerson.getIdent()).toString());

                                String tilfeldigYrke = hentTilfeldigYrkeskode(yrkeskoder);

                                if(ansettPerson(tilfeldigPerson.getIdent(),
                                        organisasjon.getOrgnummer(),
                                        tilfeldigYrke).is2xxSuccessful()){
                                    ansattePersoner.add(tilfeldigPerson);
                                    antallPersAnsatt++;
                                }
                            }
                            muligePersoner.remove(tilfeldigIndex);
                        } catch (NullPointerException e) {
                            muligePersoner = hentPersoner(from, to, organisasjon.getPostadresse().getPostnr());
                        } catch (Exception e) {
                            log.error(e.toString());
                            break;
                        }
                    }

                    log.info("Personer ansatt i org {}, {}: {}", organisasjon.getNavn(),
                            organisasjon.getOrgnummer(), ansattePersoner);
                    for (Ident person : ansattePersoner) {
                        log.info(arbeidsforholdService.hentArbeidsforhold(person.getIdent()).toString());
                    }
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
                if (arbeidsforhold.getAnsettelsesperiode().getPeriode().getTom() != null) {
                    return false;
                }
            }
        }
        return true;
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
}
