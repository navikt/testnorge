package no.nav.no.registere.testnorge.arbeidsforholdexportapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;

import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.ArbeidsforholdSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.converter.csv.PermisjonSyntetiseringCsvPrinterConverter;
import no.nav.no.registere.testnorge.arbeidsforholdexportapi.repository.InntektsmottakerHendelseRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdExportService {
    public static final int PAGE_SIZE = 500_000;
    private final InntektsmottakerHendelseRepository inntektsmottakerHendelseRepository;

    public void writeArbeidsforhold(PrintWriter writer) throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        var printer = new ArbeidsforholdSyntetiseringCsvPrinterConverter(writer);
        for (int page = 0; page < numberOfPages; page++) {
            log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
            printer.write(inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
        }
        printer.close();
    }

    public Integer writeArbeidsforhold(PrintWriter writer, Integer page) throws IOException {
        var count = inntektsmottakerHendelseRepository.count();
        int numberOfPages = (int) Math.ceil(count / (float) PAGE_SIZE);

        var printer = new ArbeidsforholdSyntetiseringCsvPrinterConverter(writer);
        log.info("Henter for side {}/{} med {} per side.", page + 1, numberOfPages, PAGE_SIZE);
        printer.write(inntektsmottakerHendelseRepository.getArbeidsforhold(page, PAGE_SIZE));
        printer.close();
        return numberOfPages;
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
