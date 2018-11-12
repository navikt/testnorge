package no.nav.registre.hodejegeren.testutils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.google.common.io.Resources;

public class ResourceUtils {
    public static String getResourceFileContent(String path) {
        URL fileUrl = Resources.getResource(path);
        try {
            return Resources.toString(fileUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
