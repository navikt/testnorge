package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.AaregConsumer;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
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
        log.info("Henter arbeidsforhold for ident: {}", ident);
        List<ArbeidsforholdDTO> arbeidsforhold = aaregConsumer.getArbeidsforholds(ident);
        return arbeidsforhold;
    }

    public void endreArbeidsforhold(ArbeidsforholdDTO request){
        log.info("Endrer arbeidsforhold for ident: {}", request.getArbeidsforholdId());
        aaregConsumer.endreArbeidsforhold(request);

    }

}
//Les i appen ArbeidsforholdService
