package no.nav.registre.testnav.geografiskekodeverkservice.service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.geografiskekodeverkservice.domain.Kodeverk;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@NoArgsConstructor
public class KodeverkService {

public static List<Kodeverk> getKommuner(String kommunenr) {
    final Properties kommuner = new Properties();
    var resource = new ClassPathResource("kommuner/kommuner.yaml");

    try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
        kommuner.load(stream);
    } catch (IOException e) {
        log.error("Lesing av kommuner feilet", e);
    }

    if (kommunenr != null) {
        List<Kodeverk> result = kommuner.entrySet().stream()
            .filter(item -> item.getKey().toString().equals(kommunenr))
            .map(item -> {
                Kodeverk obj = new Kodeverk();
                obj.setKode(item.getKey().toString());
                obj.setNavn(item.getValue().toString());
                return obj;
        }).collect(Collectors.toList());
        return result;
    }

    List<Kodeverk> result = kommuner.entrySet().stream().map(item -> {
        Kodeverk obj = new Kodeverk();
        obj.setKode(item.getKey().toString());
        obj.setNavn(item.getValue().toString());
        return obj;
    }).collect(Collectors.toList());
    return result;
}

public static List<Kodeverk> getLandkoder(String land) {
    final Properties landkoder = new Properties();
    var resource = new ClassPathResource("landkoder/landkoder.yaml");

    try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
        landkoder.load(stream);
    } catch (IOException e) {
        log.error("Lesing av landkoder feilet", e);
    }

    if (land != null) {
        List<Kodeverk> result = landkoder.entrySet().stream()
                .filter(item -> item.getValue().toString().equals(land))
                .map(item -> {
                    Kodeverk obj = new Kodeverk();
                    obj.setKode(item.getKey().toString());
                    obj.setNavn(item.getValue().toString());
                    return obj;
                }).collect(Collectors.toList());
        return result;
    }

    List<Kodeverk> result = landkoder.entrySet().stream().map(item -> {
        Kodeverk obj = new Kodeverk();
        obj.setKode(item.getKey().toString());
        obj.setNavn(item.getValue().toString());
        return obj;
    }).collect(Collectors.toList());
    return result;
}

public static List<Kodeverk> getPostnummer(String poststed) {
    final Properties postnummer = new Properties();
    var resource = new ClassPathResource("postnummer/postnummer.yaml");

    try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
        postnummer.load(stream);
    } catch (IOException e) {
        log.error("Lesing av postnummer feilet", e);
    }

    if (poststed != null) {
        List<Kodeverk> result = postnummer.entrySet().stream()
                .filter(item -> item.getValue().toString().equals(poststed))
                .map(item -> {
                    Kodeverk obj = new Kodeverk();
                    obj.setKode(item.getKey().toString());
                    obj.setNavn(item.getValue().toString());
                    return obj;
                }).collect(Collectors.toList());
        return result;
    }
    List<Kodeverk> result = postnummer.entrySet().stream().map(item -> {
            Kodeverk obj = new Kodeverk();
            obj.setKode(item.getKey().toString());
            obj.setNavn(item.getValue().toString());
            return obj;
        }).collect(Collectors.toList());
    return result;
}

public static List<Kodeverk> getEmbeter() {
    final Properties embeter = new Properties();
    var resource = new ClassPathResource("vergemaal/embeter.yaml");

    try (final InputStreamReader stream = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
        embeter.load(stream);
    } catch (IOException e) {
        log.error("Lesing av embeter feilet", e);
    }

    List<Kodeverk> result = embeter.entrySet().stream().map(item -> {
        Kodeverk obj = new Kodeverk();
        obj.setKode(item.getKey().toString());
        obj.setNavn(item.getValue().toString());
        return obj;
    }).collect(Collectors.toList());
    return result;
}
}
