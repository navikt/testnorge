package no.nav.registre.testnorge.arbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Arbeidsforhold;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {

    private final MapperFacade mapperFacade;

    public List<Arbeidsforhold> getArbeidsforhold(List<ArbeidsforholdDTO> arbeidsforholdDTO) {

        return mapperFacade.mapAsList(arbeidsforholdDTO, Arbeidsforhold.class);
    }
}
