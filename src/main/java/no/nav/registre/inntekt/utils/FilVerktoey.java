package no.nav.registre.inntekt.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Slf4j
@Component
public class FilVerktoey {

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

    public static File lastRessurs(String filNavn) throws IOException {
        var ressurs = FilVerktoey.class.getClassLoader().getResource(filNavn);
        if (ressurs == null) {
            log.error("Kunne ikke finne ressurs " + filNavn);
            throw new IOException("Resource not found exception");
        }
        return new File(ressurs.getFile());
    }

    public static byte[] encodeFilTilBase64Binary(File fil) {
        try {
            byte[] bytes = lastFil(fil);
            return Base64.getEncoder().encode(bytes);
        } catch (IOException ignored) {}
        return new byte[0];
    }
}
