package no.nav.registre.testnorge.arbeidsforhold.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.testnorge.arbeidsforhold.consumer.AaregConsumer;
import no.nav.registre.testnorge.arbeidsforhold.domain.Arbeidsforhold;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final AaregConsumer aaregConsumer;

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        return aaregConsumer.getArbeidsforhold(ident, orgnummer, arbeidsforholdId);
    }

    public List<Arbeidsforhold> getArbeidsforholds(String ident, String orgnummer) {
        return aaregConsumer.getArbeidsforholds(ident, orgnummer);
    }

    public List<Arbeidsforhold> getArbeidsforholds(String ident) {
        return aaregConsumer.getArbeidsforholds(ident);
    }

    public Arbeidsforhold createArbeidsforhold(Arbeidsforhold arbeidsforhold){
        return aaregConsumer.createArbeidsforhold(arbeidsforhold);
    }

}
