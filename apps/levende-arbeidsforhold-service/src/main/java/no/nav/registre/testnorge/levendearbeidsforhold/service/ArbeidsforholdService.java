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

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        log.info("Henter arbeidsforhold for ident: {}", ident);
        return aaregConsumer.hentArbeidsforhold(ident);
    }

    public void endreArbeidsforhold(Arbeidsforhold request){
        log.info("Endrer arbeidsforhold for ident: {}", request.getNavArbeidsforholdId());
        log.info(String.valueOf(request));
        request.getAnsettelsesperiode().getPeriode().setTom(LocalDate.now());
        request.getAnsettelsesperiode().setSluttaarsak("arbeidstakerHarSagtOppSelv");
        request.getAnsettelsesperiode().setVarslingskode("NAVEND");
        request.getArbeidsavtaler().forEach(
                arbeidsavtale -> {
                    arbeidsavtale.setStillingsprosent(0.0);
                });
        log.info(String.valueOf((request)));
        aaregConsumer.endreArbeidsforhold(request);

    }

    public void arbeidsforholdService(String aktoerId) {
        List<Arbeidsforhold> arbeidsforholdListe = hentArbeidsforhold(aktoerId);
        if (!arbeidsforholdListe.isEmpty()) {
            arbeidsforholdListe.forEach(
                    this::endreArbeidsforhold
                    //arbeidsforhold -> log.info(String.valueOf(arbeidsforhold))
            );
        }
    }

}
//Les i appen ArbeidsforholdService
