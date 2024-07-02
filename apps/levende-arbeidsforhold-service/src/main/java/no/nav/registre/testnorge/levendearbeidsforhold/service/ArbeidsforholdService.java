package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final HentArbeidsforholdConsumer hentArbeidsforholdConsumer;

    public List<ArbeidsforholdDTO> getArbeidsforhold(String ident) {
        List<ArbeidsforholdDTO> arbeidsforhold = hentArbeidsforholdConsumer.getArbeidsforholds(ident);
        return arbeidsforhold;
    }

}
//Les i appen ArbeidsforholdService