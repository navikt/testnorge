package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforhold.consumers.AaregConsumer;
import no.nav.registre.testnorge.levendearbeidsforhold.domain.v1.Arbeidsforhold;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Klasse som håndterer endring av aktive arbeidsforhold for en gitt aktør, ved hjelp av et aaregConsumer-objekt.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final AaregConsumer aaregConsumer;
    private final String sluttAarsaksKode = "arbeidstakerHarSagtOppSelv";
    private final String varslingsKode = "NAVEND";

    /**
     * Tar inn aktør-id og kaller på funksjonene hentArbeidsforhold og eventuelt endreArbeidsforhold.
     * @param aktoerId Aktør-id til den registrerte.
     */
    public void arbeidsforholdService(String aktoerId) {
        List<Arbeidsforhold> arbeidsforholdListe = hentArbeidsforhold(aktoerId);
        if (!arbeidsforholdListe.isEmpty()) {
            arbeidsforholdListe.forEach(
                    arbeidsforhold -> {
                        if (arbeidsforhold.getAnsettelsesperiode().getPeriode().getTom() == null){
                            endreArbeidsforhold(arbeidsforhold);
                        }
                    }
            );
        }
    }

    /**
     * Henter ut alle arbeidsforhold tilknyttet aktøren.
     * @param ident Kan være FNR, DNR eller aktør-id.
     * @return En liste med arbeidsforhold, eventuelt en tom liste dersom personen ikke har noen arbeidsforhold.
     */
    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        return aaregConsumer.hentArbeidsforhold(ident);
    }

    /**
     * Avslutter et arbeidsforhold ved å endre sluttdato til dagens dato, sette sluttårsak og varslingskode til angitte
     * verdier.
     * @param arbeidsforhold Arbeidsforholdet som endres.
     */
    public void endreArbeidsforhold(Arbeidsforhold arbeidsforhold){

        arbeidsforhold.getAnsettelsesperiode().getPeriode().setTom(LocalDate.now());
        arbeidsforhold.getAnsettelsesperiode().setSluttaarsak(sluttAarsaksKode);
        arbeidsforhold.getAnsettelsesperiode().setVarslingskode(varslingsKode);
        arbeidsforhold.getArbeidsavtaler().forEach(
                arbeidsavtale -> arbeidsavtale.setStillingsprosent(null));

        aaregConsumer.endreArbeidsforhold(arbeidsforhold);
    }
}
