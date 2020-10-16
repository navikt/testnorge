package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.IdentRepository;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    private final IdentRepository identRepository;
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    public List<Arbeidsforhold> hetArbeidsforholdForEtAntallPersoner(Integer antallPersoner) {
        Set<String> idetner = identRepository.getRandomIdenter(antallPersoner);
        List<String> xmls = inntektsmottakerHendelseRepository.getXmlFrom(idetner);
        OpplysningspliktigList opplysningspliktigList = OpplysningspliktigList.from(xmls);
        log.info("Fant arbeidsforhold på {}/{}", opplysningspliktigList.getAntallPersoner(), antallPersoner);

        return opplysningspliktigList.toArbeidsforhold();
    }
}
