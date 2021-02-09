package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.OpplysningspliktigList;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    private static final int NUMBER_OF_PAGES = 5;
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    public List<Arbeidsforhold> getArbeidsforhold() {
        var count = inntektsmottakerHendelseRepository.count();
        int size = (int)Math.ceil((double) count / NUMBER_OF_PAGES);

        List<List<Arbeidsforhold>> lists = new ArrayList<>();

        for (int page = 0; page < NUMBER_OF_PAGES; page++){
            lists.add(inntektsmottakerHendelseRepository.getArbeidsforhold(page, size));
        }
        return lists.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Permisjon> getPermisjoner() {
        return inntektsmottakerHendelseRepository.getAllPermisjoner();
    }
}
