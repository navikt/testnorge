package no.nav.dolly.budpro.organisasjonsenhet;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Service
@Slf4j
public class OrganisasjonsenhetService {

    private List<Organisasjonsenhet> data;

    @SneakyThrows
    @PostConstruct
    void postConstruct() {
        try (Scanner scanner = new Scanner(new ClassPathResource("static/organisasjonsenheter.csv").getInputStream(), StandardCharsets.UTF_8)) {
            data = new ArrayList<>();
            while (scanner.hasNextLine()) {
                Organisasjonsenhet
                        .of(scanner.nextLine().split(";"))
                        .ifPresent(organisasjonsenhet -> data.add(organisasjonsenhet));
            }
        }
        log.info("Read {} lines into memory", data.size());
    }

    List<Organisasjonsenhet> getAll() {
        return data;
    }

    public Organisasjonsenhet getRandom(Random random) {
        return data.get(random.nextInt(data.size()));
    }

}
