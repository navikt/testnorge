package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final HentArbeidsforholdConsumer hentArbeidsforholdConsumer;

    public List<Arbeidsforhold> getArbeidsforhold(String ident) {
        log.info("Henter arbeidsforhold for ident: {}", ident);
        List<Arbeidsforhold> arbeidsforhold = hentArbeidsforholdConsumer.getArbeidsforholds(ident);
        arbeidsforhold = hentArbeidsforholdConsumer.getArbeidsforholds(ident);
        return arbeidsforhold;
    }

}
//Les i appen ArbeidsforholdService
