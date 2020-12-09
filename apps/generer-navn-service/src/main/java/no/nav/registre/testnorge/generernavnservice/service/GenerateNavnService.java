package no.nav.registre.testnorge.generernavnservice.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import no.nav.registre.testnorge.generernavnservice.domain.Navn;

@Service
public class GenerateNavnService {
    private static final List<String> ADJEKTIVER = FileReader.readLinesFromResources("static/adjektiv.csv");
    private static final List<String> SUBSTANTIVER = FileReader.readLinesFromResources("static/substantiv.csv");
    private static final Random RANDOM = new Random();

    public Navn getRandomNavn() {
        return new Navn(
                convertToName(ADJEKTIVER.get(RANDOM.nextInt(ADJEKTIVER.size()))),
                convertToName(SUBSTANTIVER.get(RANDOM.nextInt(SUBSTANTIVER.size())))
        );
    }

    private String convertToName(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
