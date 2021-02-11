package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.PermisjonSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    public static final int PAGE_SIZE = 500_000;
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    public Path writeArbeidsforhold() throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        var path = Files.createTempFile("arbeidsforhold", ".csv");

        var file = path.toFile();
        log.info("Fil opprettet: {}.", path.toAbsolutePath());
        file.deleteOnExit();
        var printWriter = new PrintWriter(file);

        var printer = new ArbeidsforholdSyntetiseringCsvPrinterConverter(printWriter);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
        }
        log.info("Lukker printenr til fil {}.", path.toAbsolutePath());
        printer.close();
        return path;
    }

    public Path writeArbeidsforhold(Integer page) throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        var path = Files.createTempFile("arbeidsforhold", ".csv");

        log.info("Fil opprettet: {}.", path.toAbsolutePath());
        var file = path.toFile();
        file.deleteOnExit();
        var printWriter = new PrintWriter(file);

        var printer = new ArbeidsforholdSyntetiseringCsvPrinterConverter(printWriter);
        log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
        printer.write(inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
        log.info("Lukker printenr til fil {}.", path.toAbsolutePath());
        printer.close();
        return path;
    }


    public void writePermisjoner(PrintWriter writer) throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        var printer = new PermisjonSyntetiseringCsvPrinterConverter(writer);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getPermisjoner(page, PAGE_SIZE));
        }
        printer.close();
    }

    public Integer writePermisjoner(PrintWriter writer, Integer page) throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        var printer = new PermisjonSyntetiseringCsvPrinterConverter(writer);
        log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
        printer.write(inntektsmottakerHendelseRepository.getPermisjoner(page, PAGE_SIZE));

        printer.close();
        return numberOfPages;
    }

}
