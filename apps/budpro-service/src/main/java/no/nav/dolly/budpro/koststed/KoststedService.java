package no.nav.dolly.budpro.koststed;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

@Service
@Slf4j
public class KoststedService {

    private List<Koststed> data;

    @PostConstruct
    @SneakyThrows(IOException.class)
    void postConstruct() {
        try (Scanner scanner = new Scanner(new ClassPathResource("static/koststed.csv").getInputStream(), StandardCharsets.UTF_8)) {
            data = new ArrayList<>();
            while (scanner.hasNextLine()) {
                Koststed
                        .of(scanner.nextLine().split(";"))
                        .ifPresent(koststed -> data.add(koststed));
            }
        }
        log.info("Read {} lines into memory", data.size());
    }

    List<Koststed> getAll() {
        return data;
    }

    public Koststed getRandom(Random random) {
        return data.get(random.nextInt(data.size()));
    }

}
