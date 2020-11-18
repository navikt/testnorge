package no.nav.dolly;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PropertyReader {
    Map<String, Object> map = new HashMap<>();

    public PropertyReader readSecret(String name, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String property = reader.readLine();
            map.put(name, property);
            return this;
        } catch (FileNotFoundException e) {
            log.error("Ingen fil funnet under pathen " + path, e);
            throw new RuntimeException("Ingen fil funnet under pathen " + path);
        } catch (IOException ex) {
            log.error("Noe gikk galt under lesing av filen " + path, ex);
            throw new RuntimeException("Noe gikk galt under lesing av filen " + path);
        }
    }

    public static PropertyReader builder () {
        return new PropertyReader();
    }

    public Map<String, Object> build() {
        return map;
    }
}