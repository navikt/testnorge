package no.nav.registre.testnorge.sykemelding.util;


import java.io.InputStream;
import java.nio.charset.Charset;

public class StaticResourceLoader {

    private StaticResourceLoader() {
    }

    public static String loadAsString(String name, Charset charsets) {
        var resource = StaticResourceLoader.class.getResource("/static/" + name);

        try (InputStream inputStream = resource.openStream()) {
            return new String(inputStream.readAllBytes(), charsets);
        } catch (Exception e) {
            throw new RuntimeException("Klarer ikke å laste inn resource " + name, e);
        }
    }
}
