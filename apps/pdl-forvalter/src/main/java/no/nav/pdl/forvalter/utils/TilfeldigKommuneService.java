package no.nav.pdl.forvalter.utils;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.consumer.GeografiskeKodeverkConsumer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Service
public class TilfeldigKommuneService {

    private GeografiskeKodeverkConsumer geografiskeKodeverkConsumer;

    public TilfeldigKommuneService(GeografiskeKodeverkConsumer geografiskeKodeverkConsumer) {
        this.geografiskeKodeverkConsumer = geografiskeKodeverkConsumer;
    }

//    private static final Properties kommuner = new Properties();
//
//    static {
//        var resource = new ClassPathResource("kommuner/kommuner.yaml");
//
//        try (final InputStream stream = resource.getInputStream()) {
//            kommuner.load(stream);
//        } catch (IOException e) {
//            log.error("Lesing av kommuner feilet", e);
//        }
//    }

    public String getKommune() {
        var kommuner = geografiskeKodeverkConsumer.getKommuner("0301", "Oslo");
        return kommuner.get(1).kode();
//                keySet().stream()
//                .skip(new SecureRandom().nextInt(kommuner.size()))
//                .map(String.class::cast)
//                .findFirst()
//                .orElse(null);
    }
}
