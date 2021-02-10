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
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.PermisjonSyntetiseringCsvConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Arbeidsforhold;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    public static final int PAGE_SIZE = 400_000;
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    @SneakyThrows
    public File getArbeidsforholdToFile() {
        var count = inntektsmottakerHendelseRepository.count();

        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        File file = File.createTempFile("temp", ".csv");
        PrintWriter writer = new PrintWriter(file);

        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med {} per side.", page, numberOfPages, PAGE_SIZE);
            ArbeidsforholdSyntetiseringCsvConverter.inst().write(writer, inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
        }
        writer.close();
        file.deleteOnExit();
        return file;
    }


    @SneakyThrows
    public File getPermisjonerToFile() {
        var count = inntektsmottakerHendelseRepository.count();

        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        File file = File.createTempFile("temp", ".csv");
        PrintWriter writer = new PrintWriter(file);

        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med {} per side.", page, numberOfPages, PAGE_SIZE);
            PermisjonSyntetiseringCsvConverter.inst().write(writer, inntektsmottakerHendelseRepository.getPermisjoner(page, PAGE_SIZE));
        }
        writer.close();
        file.deleteOnExit();
        return file;
    }
}
