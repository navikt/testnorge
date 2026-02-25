package no.nav.testnav.levendearbeidsforholdservice.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdservice.consumers.AaregConsumer;
import no.nav.testnav.libs.dto.levendearbeidsforhold.v1.Arbeidsforhold;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private static final String sluttAarsaksKode = "arbeidstakerHarSagtOppSelv";
    private static final String varslingsKode = "NAVEND";

    private final AaregConsumer aaregConsumer;

    public Mono<Void> arbeidsforholdService(String aktoerId) {

        return aaregConsumer.hentArbeidsforhold(aktoerId)
                .filter(arbeidsforhold -> isNull(arbeidsforhold.getAnsettelsesperiode().getPeriode().getTom()))
                .flatMap(this::endreArbeidsforhold)
                .then();
    }

    private Mono<Void> endreArbeidsforhold(Arbeidsforhold arbeidsforhold) {

        arbeidsforhold.getAnsettelsesperiode().getPeriode().setTom(LocalDate.now());
        arbeidsforhold.getAnsettelsesperiode().setSluttaarsak(sluttAarsaksKode);
        arbeidsforhold.getAnsettelsesperiode().setVarslingskode(varslingsKode);
        arbeidsforhold.getArbeidsavtaler().forEach(
                arbeidsavtale -> arbeidsavtale.setStillingsprosent(null));

        return aaregConsumer.endreArbeidsforhold(arbeidsforhold);
    }
}
