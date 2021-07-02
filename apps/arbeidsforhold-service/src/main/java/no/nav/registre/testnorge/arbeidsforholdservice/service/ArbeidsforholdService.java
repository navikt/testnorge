package no.nav.registre.testnorge.arbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.v2.AaregConsumerV2;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Arbeidsforhold;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final AaregConsumerV2 aaregConsumer;


    private final MapperFacade mapperFacade;

    public List<Arbeidsforhold> getArbeidsforhold(String ident, String miljo) {
        List<ArbeidsforholdDTO> arbeidsforhold = aaregConsumer.getArbeidsforholds(ident, miljo);
        return arbeidsforhold  == null ? null : arbeidsforhold
                .stream()
                .map(Arbeidsforhold::new)
                .collect(Collectors.toList());
    }
}
