package no.nav.registre.testnav.geografiskekodeverkservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
@NoArgsConstructor
public class KodeverkService {
//    kommuner
    public static String getKommuner() {
        final Properties kommuner = new Properties();
        var resource = new ClassPathResource("kommuner/kommuner.yaml");

        try (final InputStream stream = resource.getInputStream()) {
            kommuner.load(stream);
        } catch (IOException e) {
            log.error("Lesing av kommuner feilet", e);
        }

        return kommuner.toString();
//        return "Oslo";
    }
//    landkoder
//    postnummer
//    embeter
}
