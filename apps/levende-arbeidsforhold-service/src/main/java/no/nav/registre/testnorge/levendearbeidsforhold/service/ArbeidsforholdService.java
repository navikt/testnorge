package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforhold.consumers.AaregConsumer;
import no.nav.registre.testnorge.levendearbeidsforhold.domain.v1.Arbeidsforhold;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final AaregConsumer aaregConsumer;
    private final String sluttAarsaksKode = "arbeidstakerHarSagtOppSelv";
    private final String varslingsKode = "NAVEND";

    public void arbeidsforholdService(String aktoerId) {
        List<Arbeidsforhold> arbeidsforholdListe = hentArbeidsforhold(aktoerId);
        if (!arbeidsforholdListe.isEmpty()) {
            arbeidsforholdListe.forEach(
                    this::endreArbeidsforhold
            );
        }
    }

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        log.info("Henter arbeidsforhold for ident: {}", ident);
        return aaregConsumer.hentArbeidsforhold(ident);
    }

    public void endreArbeidsforhold(Arbeidsforhold arbeidsforhold){
        log.info("Endrer arbeidsforhold for ident: {}", arbeidsforhold.getNavArbeidsforholdId());
        log.info(arbeidsforhold.toString());

        arbeidsforhold.getAnsettelsesperiode().getPeriode().setTom(LocalDate.now());
        arbeidsforhold.getAnsettelsesperiode().setSluttaarsak(sluttAarsaksKode);
        arbeidsforhold.getAnsettelsesperiode().setVarslingskode(varslingsKode);
        arbeidsforhold.getArbeidsavtaler().forEach(
                arbeidsavtale -> {
                    arbeidsavtale.setStillingsprosent(null);
                });

        log.info(arbeidsforhold.toString());
        aaregConsumer.endreArbeidsforhold(arbeidsforhold);
    }
}
