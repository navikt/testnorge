package no.nav.registre.inntekt.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class FileLoader {

    private static final String DUMMY_PDF_FILE_PATH = "static/dummy.pdf";
    private static FileLoader instance;
    private byte[] dummyPDF;

    public static FileLoader inst() {
        if (instance == null) {
            instance = new FileLoader();
        }
        return instance;
    }

    public byte[] getDummyPDF() {
        if (dummyPDF == null) {
            try {
                InputStream input = LoadResource(DUMMY_PDF_FILE_PATH);
                dummyPDF = IOUtils.toByteArray(input);
                input.close();
                log.info("{} lasted inn med byte lengde {}.", DUMMY_PDF_FILE_PATH, dummyPDF.length);
            } catch (IOException e) {
                log.error("Klarer ikke aa laste inn {}.", DUMMY_PDF_FILE_PATH, e);
                throw new RuntimeException(e);
            }
        }
        return dummyPDF;
    }

    private InputStream LoadResource(String path) {
        return FileLoader.class.getClassLoader().getResourceAsStream(path);
    }
}