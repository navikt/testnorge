package no.nav.registre.testnorge.levendearbeidsforhold.service;

import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class GenerateNavnService {
    private static final List<String> ADJEKTIVER = FileReader.readLinesFromResources("static/adjektiv.csv");
    private static final List<String> ADVERBER = FileReader.readLinesFromResources("static/adverb.csv");
    private static final List<String> SUBSTANTIVER = FileReader.readLinesFromResources("static/substantiv.csv");

    public List<NavnDTO> getRandomNavn(Long seed, Integer antall) {
        var random = seed == null ? new SecureRandom() : new Random(seed);
        return IntStream
                .range(0, antall).boxed()
                .map(navn -> NavnDTO.builder()
                        .adjektiv(toTitleCase(ADJEKTIVER.get(random.nextInt(ADJEKTIVER.size()))))
                        .adverb(toTitleCase(ADVERBER.get(random.nextInt(ADVERBER.size()))))
                        .substantiv(toTitleCase(SUBSTANTIVER.get(random.nextInt(SUBSTANTIVER.size()))))
                        .build())
                .collect(Collectors.toList());
    }

    private String toTitleCase(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}
