package no.nav.registre.inntekt.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class FilVerktoey {

    private static final InputStream dummyPdf;

    static {
        try {
            dummyPdf = FilVerktoey.lastRessurs("dummy.pdf");
        } catch (IOException e) {
            throw new RuntimeException("Kunne ikke initialisere klassen pga IOException ved lasting av dummy.pdf", e);
        }
    }

    public static byte[] encodeFilTilBase64BinaryWithDummy() {
        return encodeFilTilBase64Binary(dummyPdf);
    }

    public static InputStream lastRessurs(String filNavn) throws IOException {
        InputStream in = FilVerktoey.class.getClassLoader().getResourceAsStream(filNavn);

        if (in == null) {
            log.error("Kunne ikke finne ressurs " + filNavn);
            throw new IOException("Resource not found exception");
        }
        return in;
    }

    public static byte[] encodeFilTilBase64Binary(InputStream stream) {
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            log.error("Kunne ikke laste stream.", e);
            throw new RuntimeException(e);
        }
    }
}
