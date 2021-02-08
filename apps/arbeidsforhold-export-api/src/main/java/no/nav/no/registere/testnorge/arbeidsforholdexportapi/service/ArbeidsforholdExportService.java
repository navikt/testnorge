package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    public List<Arbeidsforhold> getArbeidsforhold() {
        return getOpplysningspliktigList().toArbeidsforhold();
    }

    private OpplysningspliktigList getOpplysningspliktigList() {
        return inntektsmottakerHendelseRepository.getAll();
    }

    public List<Permisjon> getPermisjoner() {
        return getOpplysningspliktigList().toPermisjoner();
    }
}
