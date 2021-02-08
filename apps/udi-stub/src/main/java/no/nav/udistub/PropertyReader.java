package no.nav.udistub;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PropertyReader {

    Map<String, Object> map = new HashMap<>();

    public static PropertyReader builder() {
        return new PropertyReader();
    }

    public PropertyReader readSecret(String name, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String property = reader.readLine();
            map.put(name, property);
            return this;
        } catch (IOException e) {
            log.error("Ingen fil funnet under pathen " + path, e);
            return this;
        }
    }

    public Map<String, Object> build() {
        return map;
    }
}