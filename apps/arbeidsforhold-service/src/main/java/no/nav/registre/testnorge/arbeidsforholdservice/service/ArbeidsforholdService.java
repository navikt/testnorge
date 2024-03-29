package no.nav.registre.testnorge.arbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.AaregConsumer;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.Arbeidsforhold;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final AaregConsumer aaregConsumer;

    public List<Arbeidsforhold> getArbeidsforhold(String ident, String miljo) {
        List<ArbeidsforholdDTO> arbeidsforhold = aaregConsumer.getArbeidsforholds(ident, miljo);
        return arbeidsforhold == null ? null : arbeidsforhold
                .stream()
                .map(Arbeidsforhold::new)
                .toList();
    }


    public Optional<Arbeidsforhold> getArbeidsforhold(String ident, String orgnummer, String arbeidsforholdId, String miljo) {
        return aaregConsumer
                .getArbeidsforhold(ident, orgnummer, arbeidsforholdId, miljo)
                .map(Arbeidsforhold::new);
    }

}
