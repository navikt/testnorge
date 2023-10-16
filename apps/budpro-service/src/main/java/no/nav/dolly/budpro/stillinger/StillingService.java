package no.nav.dolly.budpro.stillinger;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.budpro.koststed.Koststed;
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
public class StillingService {

    private List<Stilling> data;

    @PostConstruct
    @SneakyThrows(IOException.class)
    void postConstruct() {
        try (Scanner scanner = new Scanner(new ClassPathResource("static/stillinger.csv").getInputStream(), StandardCharsets.UTF_8)) {
            data = new ArrayList<>();
            while (scanner.hasNextLine()) {
                Stilling
                        .of(scanner.nextLine().split(";"))
                        .ifPresent(stilling -> data.add(stilling));
            }
        }
        log.info("Read {} lines into memory", data.size());
    }

    List<Stilling> getAll() {
        return data;
    }

    public Stilling getRandom(Random random) {
        return data.get(random.nextInt(data.size()));
    }

}
