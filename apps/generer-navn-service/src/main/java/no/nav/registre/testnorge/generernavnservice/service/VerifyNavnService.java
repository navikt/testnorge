package no.nav.registre.testnorge.generernavnservice.service;

import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class VerifyNavnService {
    private static final List<String> ADJEKTIVER = FileReader.readLinesFromResources("static/adjektiv.csv");
    private static final List<String> ADVERBER = FileReader.readLinesFromResources("static/adverb.csv");
    private static final List<String> SUBSTANTIVER = FileReader.readLinesFromResources("static/substantiv.csv");

    public boolean verifyNavn(NavnDTO navnDTO) {

        return (isNull(navnDTO.getAdjektiv()) ||
                ADJEKTIVER.stream().anyMatch(adjektiv -> adjektiv.equalsIgnoreCase(navnDTO.getAdjektiv()))) &&
                (isNull(navnDTO.getAdverb()) ||
                        ADVERBER.stream().anyMatch(adverb -> adverb.equalsIgnoreCase(navnDTO.getAdverb()))) &&
                (isNull(navnDTO.getSubstantiv()) ||
                        SUBSTANTIVER.stream().anyMatch(substantiv -> substantiv.equalsIgnoreCase(navnDTO.getSubstantiv())));
    }
}
