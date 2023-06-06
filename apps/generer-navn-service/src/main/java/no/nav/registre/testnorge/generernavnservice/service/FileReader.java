package no.nav.registre.testnorge.generernavnservice.service;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class FileReader {

    private static InputStreamReader readFromResources(String path)
            throws IOException {
        try (var in = FileReader.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new IOException("Unable to find file " + path);
            }
            return new InputStreamReader(in, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    static List<String> readLinesFromResources(String path) {
        try (BufferedReader reader = new BufferedReader(readFromResources(path))) {
            var lines = new ArrayList<String>();
            while (reader.ready()) {
                lines.add(reader.readLine().trim());
            }
            return lines;
        }
    }

}
