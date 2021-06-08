package no.nav.registre.testnav.inntektsmeldingservice.domain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FilLaster {

    private static final String DUMMY_PDF_FILE_PATH = "static/dummy.pdf";
    private static FilLaster instans;
    private byte[] dummyPDF;

    public static FilLaster instans() {
        if (instans == null) {
            instans = new FilLaster();
        }
        return instans;
    }

    public byte[] hentDummyPDF() {
        if (dummyPDF == null) {
            try {
                InputStream input = lastRessurs(DUMMY_PDF_FILE_PATH);
                dummyPDF = IOUtils.toByteArray(input);
                input.close();
                log.info("{} lastet inn med byte lengde {}.", DUMMY_PDF_FILE_PATH, dummyPDF.length);
            } catch (IOException e) {
                log.error("Klarer ikke å laste inn {}.", DUMMY_PDF_FILE_PATH, e);
                throw new RuntimeException(e);
            }
        }
        return dummyPDF;
    }

    private InputStream lastRessurs(String path) {
        return FilLaster.class.getClassLoader().getResourceAsStream(path);
    }
}