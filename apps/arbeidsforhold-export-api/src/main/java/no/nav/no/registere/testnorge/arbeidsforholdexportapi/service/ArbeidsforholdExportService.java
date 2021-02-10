package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    @SneakyThrows
    public void getArbeidsforhold(PrintWriter writer) {
        var count = inntektsmottakerHendelseRepository.count();

        int numberOfPages = (int) Math.ceil(count / 400_000f);

        int size = (int) Math.ceil((double) count / numberOfPages);

        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med {} per side.", page, numberOfPages, page);
            ArbeidsforholdSyntetiseringCsvConverter.inst().write(writer, inntektsmottakerHendelseRepository.getArbeidsforhold(page, size));
        }
    }


    public List<Permisjon> getPermisjoner() {
        return inntektsmottakerHendelseRepository.getAllPermisjoner();
    }
}
