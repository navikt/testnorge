package no.nav.registre.inntekt.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
public class FilVerktoey {


    private static File DUMMY_PDF;
    private static final String DUMMY_PDF_FILE_NAME = "dummy.pdf";

    static {
        try {
            log.info("Laster inn {}...", DUMMY_PDF_FILE_NAME);
            DUMMY_PDF = FilVerktoey.lastRessurs(DUMMY_PDF_FILE_NAME);
            log.info("{} lasted inn.", DUMMY_PDF_FILE_NAME);
        } catch (IOException e) {
            log.error("Feil ved innlasting av {}", DUMMY_PDF_FILE_NAME, e);
            //throw new RuntimeException("Kunne ikke initialisere klassen pga IOException ved lasting av dummy.pdf", e);
        }
    }

    public static byte[] encodeFilTilBase64BinaryWithDummy() {
        return encodeFilTilBase64Binary(DUMMY_PDF);
    }

    public static File lastRessurs(String filNavn) throws IOException {
        URL resource = FilVerktoey.class.getClassLoader().getResource("static/" + filNavn);
        if (resource == null) {
            log.error("Kunne ikke finne ressurs " + filNavn);
            throw new IOException("Resource not found exception");
        }
        return new File(resource.getFile());
    }

    public static byte[] encodeFilTilBase64Binary(File file) {
        try {
            return IOUtils.toByteArray(new FileInputStream(file));
        } catch (IOException e) {
            log.error("Kunne ikke laste stream.", e);
            throw new RuntimeException(e);
        }
    }
}