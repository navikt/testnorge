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

    public List<Arbeidsforhold> getArbeidsforhold() {
        var count = inntektsmottakerHendelseRepository.count();

        int numberOfPages = (int) Math.ceil(count / 800_000f);

        int size = (int) Math.ceil((double) count / numberOfPages);

        List<List<Arbeidsforhold>> lists = new ArrayList<>();


        for (int page = 0; page < numberOfPages; page++) {
            lists.add(inntektsmottakerHendelseRepository.getArbeidsforhold(page, size));
        }
        return lists.stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    @SneakyThrows
    public File getArbeidsforholdFile() {
        var count = inntektsmottakerHendelseRepository.count();

        int numberOfPages = (int) Math.ceil(count / 100_000f);

        int size = (int) Math.ceil((double) count / numberOfPages);

        List<List<Arbeidsforhold>> lists = new ArrayList<>();


        File file = File.createTempFile("temp", ".csv");

        PrintWriter writer = new PrintWriter(file);

        for (int page = 0; page < numberOfPages; page++) {
            ArbeidsforholdSyntetiseringCsvConverter.inst().write(writer, inntektsmottakerHendelseRepository.getArbeidsforhold(page, size));
        }
        writer.close();
        file.deleteOnExit();
        return file;
    }


    public List<Permisjon> getPermisjoner() {
        return inntektsmottakerHendelseRepository.getAllPermisjoner();
    }
}
