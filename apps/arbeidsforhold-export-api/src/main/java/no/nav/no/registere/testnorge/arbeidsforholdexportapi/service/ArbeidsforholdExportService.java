package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.PrintWriter;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.PermisjonSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    public static final int PAGE_SIZE = 10_000;
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    @SneakyThrows
    public File getArbeidsforholdToFile() {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        numberOfPages = 2;

        File file = File.createTempFile("temp", null);
        log.info("Lagerer arbeidsforhold til fil: {}", file.getPath());
        file.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(file)) {
            var printer = new ArbeidsforholdSyntetiseringCsvPrinterConverter(writer);
            for (int page = 0; page < numberOfPages; page++) {
                log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
                printer.write(inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
            }
        }
        return file;
    }


    @SneakyThrows
    public File getPermisjonerToFile() {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        File file = File.createTempFile("temp", null);
        log.info("Lagerer permisjoner til fil: {}", file.getPath());
        file.deleteOnExit();
        try (PrintWriter writer = new PrintWriter(file)) {
            var printer = new PermisjonSyntetiseringCsvPrinterConverter(writer);
            for (int page = 0; page < numberOfPages; page++) {
                log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
                printer.write(inntektsmottakerHendelseRepository.getPermisjoner(page, PAGE_SIZE));
            }
        }
        return file;
    }
}
