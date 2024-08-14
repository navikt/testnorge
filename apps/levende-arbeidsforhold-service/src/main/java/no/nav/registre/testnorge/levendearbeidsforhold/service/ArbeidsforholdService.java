package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.AaregConsumer;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final AaregConsumer aaregConsumer;
    private final String sluttAarsaksKode = "arbeidstakerHarSagtOppSelv";
    private final String varslingsKode = "NAVEND";

    public void arbeidsforholdService(String aktoerId) {

        hentArbeidsforhold(aktoerId)
                .forEach(
                    arbeidsforhold -> {
                        if (isNull(arbeidsforhold.getAnsettelsesperiode().getPeriode().getTom()) {
                            endreArbeidsforhold(arbeidsforhold);
                        }
                    }
            );
        }
    }

    public List<Arbeidsforhold> hentArbeidsforhold(String ident) {
        return aaregConsumer.hentArbeidsforhold(ident);
    }

    public void endreArbeidsforhold(Arbeidsforhold arbeidsforhold){

        arbeidsforhold.getAnsettelsesperiode().getPeriode().setTom(LocalDate.now());
        arbeidsforhold.getAnsettelsesperiode().setSluttaarsak(sluttAarsaksKode);
        arbeidsforhold.getAnsettelsesperiode().setVarslingskode(varslingsKode);
        arbeidsforhold.getArbeidsavtaler().forEach(
                arbeidsavtale -> arbeidsavtale.setStillingsprosent(null));

        aaregConsumer.endreArbeidsforhold(arbeidsforhold);
    }
}
