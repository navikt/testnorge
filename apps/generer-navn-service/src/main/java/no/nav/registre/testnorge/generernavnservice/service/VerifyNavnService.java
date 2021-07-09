package no.nav.registre.testnorge.generernavnservice.service;

import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Objects.isNull;

@Service
public class VerifyNavnService {
    private static final List<String> ADJEKTIVER = FileReader.readLinesFromResources("static/adjektiv.csv");
    private static final List<String> ADVERBER = FileReader.readLinesFromResources("static/adverb.csv");
    private static final List<String> SUBSTANTIVER = FileReader.readLinesFromResources("static/substantiv.csv");

    private static boolean verify(String value, List<String> approvedValues) {

        return isNull(value) || approvedValues.stream().anyMatch(validValue -> validValue.equalsIgnoreCase(value));
    }

    public boolean verifyNavn(NavnDTO navnDTO) {

        return verify(navnDTO.getAdjektiv(), ADJEKTIVER) &&
                verify(navnDTO.getAdverb(), ADVERBER) &&
                verify(navnDTO.getSubstantiv(), SUBSTANTIVER);
    }
}
