package no.nav.registre.testnorge.generernavnservice.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import no.nav.registre.testnorge.generernavnservice.domain.Navn;

@Service
public class GenerateNavnService {
    private static final List<String> ADJEKTIVER = FileReader.readLinesFromResources("static/adjektiv.csv");
    private static final List<String> SUBSTANTIVER = FileReader.readLinesFromResources("static/substantiv.csv");
    private static final Random RANDOM = new Random();

    public List<Navn> getRandomNavn(Integer antall) {

        return IntStream.range(0, antall).boxed()
                .map(navn -> Navn.builder()
                .adjektiv(toTitleCase(ADJEKTIVER.get(RANDOM.nextInt(ADJEKTIVER.size()))))
                .substantiv(toTitleCase(SUBSTANTIVER.get(RANDOM.nextInt(SUBSTANTIVER.size()))))
                        .build())
                .collect(Collectors.toList());
    }

    private String toTitleCase(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
