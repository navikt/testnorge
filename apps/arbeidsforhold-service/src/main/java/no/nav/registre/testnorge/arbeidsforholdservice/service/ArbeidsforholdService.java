package no.nav.registre.testnorge.arbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.arbeidsforholdservice.consumer.AaregConsumer;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.Arbeidsforhold;

@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final AaregConsumer aaregConsumer;

    public Arbeidsforhold getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId) {
        return aaregConsumer.getArbeidsforhold(ident, orgnummer, arbeidsforholdId);
    }
}
