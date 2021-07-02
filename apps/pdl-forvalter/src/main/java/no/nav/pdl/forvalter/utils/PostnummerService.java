package no.nav.pdl.forvalter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
public class PostnummerService {

    private static final Properties postnumre = new Properties();

    static {
        var resource = new ClassPathResource("postnummer/postnummer.yaml");

        try (final InputStream stream = resource.getInputStream()) {
            postnumre.load(stream);
        } catch (IOException e) {
            log.error("Lesing av landkoder feilet", e);
        }
    }

    public String getNavn(String postnummer) {
        return (String) postnumre.getOrDefault(postnummer, "UKJENT");
    }
}
