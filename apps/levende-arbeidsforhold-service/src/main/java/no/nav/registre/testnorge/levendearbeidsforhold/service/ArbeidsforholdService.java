package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.AaregConsumer;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final AaregConsumer aaregConsumer;

    public List<ArbeidsforholdDTO> getArbeidsforhold(String ident) {
        return aaregConsumer.getArbeidsforholds(ident);
    }
    public Mono<Arbeidsforhold> endreArbeidsforhold(Arbeidsforhold requests) {
        return aaregConsumer.endreArbeidsforhold(requests);
    }
}
//Les i appen ArbeidsforholdService