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
}
