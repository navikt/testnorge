package no.nav.dolly.budpro.ansettelsestype;

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
public class AnsettelsestypeService {

    private List<String> data;

    @PostConstruct
    @SneakyThrows(IOException.class)
    void postConstruct() {
        try (Scanner scanner = new Scanner(new ClassPathResource("static/ansettelsestype.txt").getInputStream(), StandardCharsets.UTF_8)) {
            data = new ArrayList<>();
            while (scanner.hasNextLine()) {
                data.add(scanner.nextLine());
            }
        }
        log.info("Read {} lines into memory", data.size());
    }

    List<String> getAll() {
        return data;
    }

    public String getRandom(Random random) {
        return data.get(random.nextInt(data.size()));
    }

}
