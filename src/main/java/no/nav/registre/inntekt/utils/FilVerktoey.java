package no.nav.registre.inntekt.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class FilVerktoey {

    private static final File dummyPdf;

    static {
        try {
            dummyPdf = FilVerktoey.lastRessurs("dummy.pdf");
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke initialisere klassen pga IOException ved lasting av dummy.pdf", e);
        }
    }

    private static byte[] lastFil(File fil) throws IOException {
        try (InputStream is = new FileInputStream(fil)) {

            long length = fil.length();
            if (length > Integer.MAX_VALUE) {
                log.warn("Fil for stor for å leses.");
                throw new IOException("File too large");
            }
            byte[] bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            is.close();
            return bytes;
        }
    }


    public static byte[] encodeFilTilBase64BinaryWithDummy() {
        return encodeFilTilBase64Binary(dummyPdf);
    }

    public static File lastRessurs(String filNavn) throws IOException {
        var ressurs = Thread.currentThread().getContextClassLoader().getResource(filNavn);
        if (ressurs == null) {
            log.error("Kunne ikke finne ressurs " + filNavn);
            throw new IOException("Resource not found exception");
        }
        return new File(ressurs.getFile());
    }

    public static byte[] encodeFilTilBase64Binary(File fil) {
        try {
            return IOUtils.toByteArray(new FileInputStream(fil));
        } catch (IOException e) {
            log.error("Kunne ikke laste fil.", e);
            throw new RuntimeException(e);
        }
    }
}
