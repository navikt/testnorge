package no.nav.registre.inntekt.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.inntekt.exception.FileLoaderIOException;

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
                InputStream input = loadResource(DUMMY_PDF_FILE_PATH);
                dummyPDF = IOUtils.toByteArray(input);
                input.close();
                log.info("{} lasted inn med byte lengde {}.", DUMMY_PDF_FILE_PATH, dummyPDF.length);
            } catch (IOException e) {
                log.error("Klarer ikke aa laste inn {}.", DUMMY_PDF_FILE_PATH, e);
                throw new FileLoaderIOException(e);
            }
        }
        return dummyPDF;
    }

    private InputStream loadResource(String path) {
        return FileLoader.class.getClassLoader().getResourceAsStream(path);
    }
}