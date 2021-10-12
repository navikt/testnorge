package no.nav.registre.sigrun.testutils;

import wiremock.com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResourceUtils {

    public static String getResourceFileContent(String path) {
        var fileUrl = Resources.getResource(path);
        try {
            return Resources.toString(fileUrl, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
