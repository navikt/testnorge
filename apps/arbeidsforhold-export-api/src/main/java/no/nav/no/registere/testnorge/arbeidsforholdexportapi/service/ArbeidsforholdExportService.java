package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.AvvikSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.InntektSyntetiseringCsvPrinterConverter;
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

        if (numberOfPages > 1) {
            log.warn("Deler opp operasjonen i {} deler for å unngå minne problemer.", numberOfPages);
        }
        var path = Files.createTempFile("arb-" + System.currentTimeMillis() + "-", ".csv");

        var file = path.toFile();
        log.info("Fil opprettet: {}.", path);
        file.deleteOnExit();
        var printWriter = new PrintWriter(file);

        var printer = new ArbeidsforholdSyntetiseringCsvPrinterConverter(printWriter);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med opp til {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
            printWriter.flush();
        }
        log.info("Lukker printeren til fil {}.", path.toAbsolutePath());
        printer.close();
        return path;
    }


    public Path writePermisjoner() throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        if (numberOfPages > 1) {
            log.warn("Deler opp operasjonen i {} deler for å unngå minne problemer.", numberOfPages);
        }
        var path = Files.createTempFile("prm-" + System.currentTimeMillis() + "-", ".csv");

        var file = path.toFile();
        log.info("Fil opprettet: {}.", path);
        file.deleteOnExit();
        var printWriter = new PrintWriter(file);

        var printer = new PermisjonSyntetiseringCsvPrinterConverter(printWriter);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med opp til {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getPermisjoner(page, PAGE_SIZE));
            printWriter.flush();
        }
        log.info("Lukker printeren til fil {}.", path);
        printer.close();
        return path;
    }

    public Path writeInntekter() throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        if (numberOfPages > 1) {
            log.warn("Deler opp operasjonen i {} deler for å unngå minne problemer.", numberOfPages);
        }
        var path = Files.createTempFile("ink-" + System.currentTimeMillis() + "-", ".csv");

        var file = path.toFile();
        log.info("Fil opprettet: {}.", path);
        file.deleteOnExit();
        var printWriter = new PrintWriter(file);

        var printer = new InntektSyntetiseringCsvPrinterConverter(printWriter);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med opp til {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getInntekter(page, PAGE_SIZE));
            printWriter.flush();
        }
        log.info("Lukker printeren til fil {}.", path);
        printer.close();
        return path;
    }

    public Path writeAvvik() throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        if (numberOfPages > 1) {
            log.warn("Deler opp operasjonen i {} deler for å unngå minne problemer.", numberOfPages);
        }
        var path = Files.createTempFile("avk-" + System.currentTimeMillis() + "-", ".csv");

        var file = path.toFile();
        log.info("Fil opprettet: {}.", path);
        file.deleteOnExit();
        var printWriter = new PrintWriter(file);

        var printer = new AvvikSyntetiseringCsvPrinterConverter(printWriter);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med opp til {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getAvvik(page, PAGE_SIZE));
            printWriter.flush();
        }
        log.info("Lukker printeren til fil {}.", path);
        printer.close();
        return path;
    }
}
